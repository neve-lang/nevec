package stage.travel.impl

import cli.Options
import nevec.result.Aftermath
import stage.Stage

/**
 * Coordinates the process of going through multiple compilation stages, threading an [Aftermath] along the way.
 *
 * It provides the following methods:
 *
 * - A [proceedWith] method that performs a monadic operation on `this` [Travel], returning a new one which contains
 *   the result of the previous [Stage].
 * - An [ifEnabled] method that performs the given [Stage] **if and only if** the given option is enabled, returning
 *   the next [Travel].
 * - An [alsoIfEnabled] method that performs the given callback **if and only if** the given option is enabled,
 *   returning `this` [Travel].
 * - A [finish] method that returns an [Aftermath] encoding whether all stages were successful.
 */
interface Travel<T> {
    /**
     * @return A new [Travel] with the result of the given compilation stage.
     *
     * If `this` [Travel]’s [Aftermath] is [OfFail][Aftermath.OfFail], the next stage is not performed—a
     * [frozen travel] is returned instead.
     *
     * @see nevec.travel.FrozenTravel
     */
    fun <Out> proceedWith(stage: () -> Stage<T, Out>): Travel<Out>

    /**
     * Performs the given [stage] if [option] is enabled.
     *
     * @return A new [Travel] containing the result.
     */
    fun ifEnabled(option: Options, stage: () -> Stage<T, T>): Travel<T>

    /**
     * Performs the given [callback] if [option] is enabled.
     *
     * @return `this` [Travel].
     */
    fun alsoIfEnabled(option: Options, callback: () -> Unit): Travel<T>

    /**
     * @return An [Aftermath] determining whether all stages were successful.  That is:
     *
     * - If `this` is [alive][nevec.travel.AliveTravel], an [Aftermath] of [Success][Aftermath.Success] is returned,
     * containing the [Travel]’s data.
     * - Otherwise, an [Aftermath] of [OfFail][Aftermath.OfFail] is returned based on the
     *   [frozen][nevec.travel.FrozenTravel] travel.
     */
    fun finish(): Aftermath<T>
}