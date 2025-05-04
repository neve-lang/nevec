package tok

/**
 * Represents a stream of tokens, implemented as an iterator.
 */
data class TokStream(private val list: MutableList<Tok>) : Iterator<Tok> {
    override fun hasNext(): Boolean {
        return list.isEmpty()
    }

    override fun next(): Tok {
        return list.removeFirst()
    }
}