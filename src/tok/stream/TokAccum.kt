package tok.stream

import tok.Tok

/**
 * Similar to a [TokStream] builder—it uses a [MutableList] to accumulate each token, before [building][build] it into
 * a [TokStream].
 */
class TokAccum(private val accum: MutableList<Tok>) {
    /**
     * Adds a new [Tok] to the accumulator, mutating the internal mutable list.
     */
    fun add(new: Tok) {
        accum.add(new)
    }

    /**
     * Builds the [TokAccum] into a [TokStream].
     *
     * @return An immutable [TokStream] with the tokens accumulated.
     */
    fun build(): TokStream {
        return TokStream(accum.toList())
    }
}