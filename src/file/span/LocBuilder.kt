package file.span

import file.module.Module

/**
 * Simplifies the process of building a [Loc].
 */
class LocBuilder {
    companion object {
        fun from(other: Loc) = other.asBuilder()
    }

    private var col: UInt? = null
    private var line: UInt? = null
    private var len: UInt? = null

    fun line(line: UInt) = apply { this.line = line }
    fun col(col: UInt) = apply { this.col = col }
    fun len(len: UInt) = apply { this.len = len }

    fun build(): Loc {
        require(col != null) { "A col must be provided" }
        require(line != null) { "A line must be provided" }
        require(len != null) { "A length must be provided" }

        return Loc(Module.curr(), col!!, line!!, len!!)
    }
}