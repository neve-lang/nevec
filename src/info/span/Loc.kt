package info.span

data class Loc(
    val col: UInt,
    val line: UInt,
    val len: UInt,
    val termWidth: UInt = len,
    val termCol: UInt = col
) {
    companion object {
        fun onLine(number: UInt) = Loc(1u, number, 1u, 1u, 1u)
    }

    fun begin() = col
    fun end() = col + len

    fun asBuilder() = LocBuilder().col(col).line(line).len(len).termCol(termCol).termWidth(termWidth)
    fun extremes() = Pair(begin(), end())

    override fun toString() = "$line:$col"
}