package stage.travel

import cli.Options
import nevec.result.Aftermath
import nevec.result.Fail
import stage.Stage
import stage.travel.impl.Travel

/**
 * Variant of [Travel] that is considered **frozen**—i.e. a failure occurred, and compilation may not proceed.
 */
data class FrozenTravel<T>(val fail: Fail) : Travel<T> {
    override fun <Out> proceedWith(stage: () -> Stage<T, Out>): Travel<Out> {
        return FrozenTravel(fail)
    }

    override fun ifEnabled(option: Options, stage: () -> Stage<T, T>): Travel<T> {
        return FrozenTravel(fail)
    }

    override fun alsoIfEnabled(option: Options, callback: () -> Unit): Travel<T> {
        return this
    }

    override fun finish(): Aftermath<T> {
        return Aftermath.OfFail(fail)
    }
}