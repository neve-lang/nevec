package info.span

data class Loc(
    val col: UInt,
    val line: UInt,
    val len: UInt,
    val termWidth: UInt = len,
    val termCol: UInt = col
)