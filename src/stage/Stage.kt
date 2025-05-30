package stage

import ctx.Ctx
import nevec.result.Aftermath

/**
 * Represents a single compilation stage, such as the [parsing stage][parse.Parse] or the [IR lowering][ir.lower.Lower]
 * stage.
 *
 * @param In The type of value that the compilation stage requires in order to [perform].
 * @param Out The type of value that the compilation stage should return.
 *
 * It provides a [perform] method that returns an [Aftermath] of the specified type [T].
 */
interface Stage<In, Out> {
    /**
     * @return An [Aftermath] that can either be successful or unsuccessful, depending on how the compilation stage
     * goes.
     */
    fun perform(data: In, ctx: Ctx): Aftermath<Out>
}