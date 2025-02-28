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

    override fun toString() = "$line:$col"
}