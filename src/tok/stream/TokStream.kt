package tok.stream

import tok.Tok

/**
 * Represents a stream of tokens.
 *
 * A [TokStream] may only be used for one individual Neve module.
 */
data class TokStream(val toks: MutableList<Tok>) {
    /**
     * @return The next [Tok] in the [TokStream].
     *
     * If there are no tokens left, an [EOF token][Tok.eof] is returned.
     */
    fun next(): Tok {
        return if (toks.isNotEmpty())
            toks.first().also { toks.removeFirst() }
        else
            Tok.eof()
    }
}