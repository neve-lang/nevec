package file.span

data class Loc(
    var col: UInt,
    var line: UInt,
    var len: UInt,
) {
    companion object {
        fun new() = Loc(1u, 1u, 0u)
        fun onLine(number: UInt) = Loc(1u, number, 1u)
    }

    fun end() = col + len

    fun asBuilder() = LocBuilder().col(col).line(line).len(len)

    fun extremes() = Pair(begin(), end())

    fun copy() = asBuilder().build()

    fun advance() {
        len++
    }

    fun newline() {
        col = 0u
        line++
    }

    fun sync() {
        col += len
        len = 0u
    }

    operator fun plus(other: Loc): LocBuilder {
        val max = if (col > other.col) this else other

        val minCol = col.coerceAtMost(other.col)
        val maxCol = col.coerceAtLeast(other.col)

        val len = maxCol - minCol + max.len

        return LocBuilder.from(this).col(minCol).len(len)
    }

    private fun begin() = col

    override fun toString() = "$line:$col"
}