package file.span

import file.contents.Src
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
        /**
         * @return a [Loc] on the current [Module], with [col] 1, [line] 1, and [len] 0.
         *
         * @see Module
         */
        fun new(): Loc {
            return Loc(Module.curr(), 1u, 1u, 0u)
        }

        /**
         * @return a [Loc] on the current [Module], with [col] 1, [line] set to [number], and [len] 0.
         */
        fun onLine(number: UInt): Loc {
            return Loc(Module.curr(), 1u, number, 1u)
        }
    }

    /**
     * Increments this [Loc]’s [len].
     */
    fun advance() {
        len++
    }

    /**
     * Sets [col] to 0 and increments this [Loc]’s [line].
     */
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
     * @return the lexeme being represented by this [Loc].
     */
    fun lexeme(): String {
        return Src.lexeme(at = this)
    }

    /**
     * @return the line being represented by this [Loc].
     */
    fun line(): String {
        return Src.line(at = this)
    }

    /**
     * @return the column where this [Loc] ends, i.e. `col + len`.
     */
    fun end(): UInt {
        return col + len
    }

    /**
     * @return a [Pair] containing [begin] as its first member, and [end] as its last member.
     */
    fun extremes(): Pair<UInt, UInt> {
        return Pair(begin(), end())
    }

    /**
     * @return a [LocBuilder] with this [Loc]’s [col], [line], and [len].
     *
     * @see LocBuilder
     */
    fun asBuilder(): LocBuilder {
        return LocBuilder().col(col).line(line).len(len)
    }

    /**
     * @return an exact copy of this [Loc].
     */
    fun copy(): Loc {
        return asBuilder().build()
    }

    /**
     * Tries to merge two optional [Loc]s if possible.
     *
     * @return a non-null [Loc] that is:
     *  * equal to [Loc.new] if either [this] or the argument given are null
     *  * equal to [this] plus the argument given, **as a built LocBuilder** (i.e. it returns a [Loc], unlike [Loc.plus])
     */
    fun Loc?.tryMerge(with: Loc?): Loc {
        if (this == null || with == null) {
            return new()
        }

        return (this + with).build()
    }

    /**
     * Tries to merge two [Loc]s if possible, where `this` is non-null.
     *
     * @return a non-null [Loc] that is:
     *  * equal to [Loc.new] if the argument given is null
     *  * equal to `this` plus the argument given, **as a built LocBuilder** (i.e. it returns a [Loc], unlike [Loc.plus])
     */
    fun tryMerge(with: Loc?): Loc {
        return if (with == null)
            new()
        else
            (this + with).build()
    }

    /**
     * Adds two [Locs][Loc] as a **convex hull**.
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

    override fun toString(): String {
        return "$line:$col"
    }

    private fun begin(): UInt {
        return col
    }
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