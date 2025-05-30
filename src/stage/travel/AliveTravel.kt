package stage.travel

import cli.Options
import ctx.Ctx
import nevec.result.Aftermath
import stage.Stage
import stage.travel.impl.Travel

/**
 * Variant of [Travel] that represents an **alive** [Travel]—that is, a non-frozen one.
 *
 * @see FrozenTravel
 */
data class AliveTravel<T>(val data: T, val ctx: Ctx) : Travel<T> {
    override fun <Out> proceedWith(stage: () -> Stage<T, Out>) = when (val out = stage().perform(data, ctx)) {
        is Aftermath.Success -> AliveTravel(data = out.result, ctx)
        is Aftermath.OfFail -> FrozenTravel(fail = out.fail)
    }

    override fun ifEnabled(option: Options, stage: () -> Stage<T, T>): Travel<T> {
        return if (ctx.isEnabled(option))
            proceedWith(stage)
        else
            this
    }

    override fun alsoIfEnabled(option: Options, callback: () -> Unit): Travel<T> {
        if (ctx.isEnabled(option)) {
            callback()
        }

        return this
    }

    override fun finish(): Aftermath<T> {
        return Aftermath.Success(data)
    }
}
