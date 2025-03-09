package file.span

import err.report.Report
import file.module.Module

/**
 * Denotes a span of code in a given [Module].
 */
data class Loc(
    val module: Module,
    var col: UInt,
    var line: UInt,
    var len: UInt,
) {
    companion object {
        fun new() = Loc(Module.curr(), 1u, 1u, 0u)
        fun onLine(number: UInt) = Loc(Module.curr(), 1u, number, 1u)
    }

    fun advance() {
        len++
    }

    fun newline() {
        col = 0u
        line++
    }

    /**
     * Resets the position of the current Loc to the last visited column.
     *
     * # Example
     *
     * Before calling [sync]:
     *
     * ```
     *   let some = thing
     *   ^^^^^ the current Loc spans this space.
     * ```
     *
     * After calling [sync]:
     *
     * ```
     *   let some = thing
     *       ^ the current Loc spans this space.
     * ```
     */
    fun sync() {
        col += len
        len = 0u
    }

    /**
     * Adds two Locs as a **convex hull**.
     *
     * # Example
     *
     * Assume two locs, `a` and `b`:
     *
     * ```
     *   let some = thing
     *   ^^^ a    ^ b
     * ```
     *
     * This implies that `a + b` will correspond to:
     *
     * ```
     *   let some = thing
     *   ^^^^^^^^^^ a + b
     * ```
     */
    operator fun plus(other: Loc): LocBuilder {
        val max = if (col > other.col) this else other

        val minCol = col.coerceAtMost(other.col)
        val maxCol = col.coerceAtLeast(other.col)

        val len = maxCol - minCol + max.len

        return LocBuilder.from(this).col(minCol).len(len)
    }

    fun lexeme() = Report.lexeme(at = this)

    fun line() = Report.line(at = this)

    fun end() = col + len

    fun asBuilder() = LocBuilder().col(col).line(line).len(len)

    fun extremes() = Pair(begin(), end())

    fun copy() = asBuilder().build()

    private fun begin() = col

    override fun toString() = "$line:$col"
}

/**
 * Converts an UInt to a zero-indexable Int.
 *
 * # Example
 *
 * ```
 * val (begin, end) = loc.extremes()
 *
 * // Kotlin will complain, *and* the substring would be offset by 1 due to the 1-indexing nature of Loc columns and lines.
 * return line.substring(begin, end)
 * ```
 *
 * But using [indexable]:
 *
 * ```
 * val (begin, end) = loc.extremes().map(UInt::indexable)
 *
 * // Expected behavior!
 * return line.substring(begin, end)
 * ```
 */
fun UInt.indexable() = (this - 1u).toInt()