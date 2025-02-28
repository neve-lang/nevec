package file.span

class LocBuilder {
    companion object {
        fun from(other: Loc) = other.asBuilder()

        fun onLine(number: UInt) = Loc(1u, number, 1u).asBuilder()
    }

    private var col: UInt? = null
    private var line: UInt? = null
    private var len: UInt? = null
    private var termWidth: UInt? = len
    private var termCol: UInt? = col

    fun line(line: UInt) = apply { this.line = line }
    fun termWidth(termWidth: UInt) = apply { this.termWidth = termWidth }
    fun termCol(termCol: UInt) = apply { this.termCol = termCol }

    fun col(col: UInt) = apply {
        this.col = col
        termCol(col)
    }

    fun len(len: UInt) = apply {
        this.len = len
        termWidth(len)
    }

    fun build(): Loc {
        require(col != null) { "A col must be provided" }
        require(line != null) { "A line must be provided" }
        require(len != null) { "A length must be provided" }

        return Loc(col!!, line!!, len!!, termWidth!!, termCol!!)
    }
}