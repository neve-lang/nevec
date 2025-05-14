package parse.err

import parse.ctx.ParseCtx

/**
 * Denotes whether parsing a chunk of code was successful or not.
 *
 * @param T The type of data that is returned by the parse module.
 *
 * @see parse.help.TinyParse
 */
sealed class ParseResult<T> {
    /**
     * Parsing the given chunk of code was successful.
     *
     * @property data The data returned by the parse module.
     * @property ctx The new [ParseCtx] after the parsing was performed, **not [popped][ParseCtx.popped]**.  Popping is
     * performed by [newCtx].
     */
    data class Success<T>(val data: T, val ctx: ParseCtx) : ParseResult<T>()

    /**
     * Parsing the given chunk of code was successful, but we still return something.
     *
     * This is useful in cases where we still want to return some identity structure when a failure occurs, like
     * [ParseMeta][parse.meta.ParseMeta] does.
     *
     * @property data The data returned by the parse module.
     * @property ctx The new [ParseCtx] after the parsing was performed, **not [popped][ParseCtx.popped]**.  Popping is
     * performed by [newCtx].
     */
    data class SemiFail<T>(val data: T, val ctx: ParseCtx) : ParseResult<T>()

    /**
     * Parsing the given chunk of code was not successful.
     *
     * @property ctx The new [ParseCtx] after the parsing was performed, **not [popped][ParseCtx.popped]**.  Popping is
     * performed by [newCtx].
     */
    data class Fail<T>(val ctx: ParseCtx) : ParseResult<T>()

    /**
     * “Unwraps” the value contained within the [ParseResult] if possible.
     *
     * @return The value wrapped by the [ParseResult] if `this` is [Success] or [SemiFail], `null` otherwise.
     */
    fun success() = when (this) {
        is Success -> data
        is SemiFail -> data
        is Fail -> null
    }

    /**
     * @return The new [ParseCtx] after parsing was performed, **[popped][ParseCtx.popped]**.
     */
    fun newCtx() = when (this) {
        is Success -> ctx.popped()
        is SemiFail -> ctx.popped()
        is Fail -> ctx.popped()
    }
}