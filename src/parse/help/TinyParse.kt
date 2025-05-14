package parse.help

import parse.ctx.ParseCtx
import parse.err.ParseResult

/**
 * Simple interface for **parse modules**, providing a [parse] method that returns a [ParseResult].
 *
 * @param In The type of data that is required by the parse module.
 * @param Out The type of data that is returned by the parse module.
 *
 * @see ParseResult
 */
interface TinyParse<In, Out> {
    /**
     * Parses user code given [ctx] and [In].
     *
     * @return A [ParseResult].
     */
    fun parse(ctx: ParseCtx, data: In): ParseResult<Out>
}