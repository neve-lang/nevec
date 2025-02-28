package file.span

data class Loc(
    val col: UInt,
    val line: UInt,
    val len: UInt,
) {
    companion object {
        fun new() = Loc(1u, 1u, 0u)
        fun onLine(number: UInt) = Loc(1u, number, 1u)
    }

    private fun begin() = col
    fun end() = col + len

    fun asBuilder() = LocBuilder().col(col).line(line).len(len)
    fun extremes() = Pair(begin(), end())

    override fun toString() = "$line:$col"
}