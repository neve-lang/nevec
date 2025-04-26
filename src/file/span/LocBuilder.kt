package file.span

import file.module.Module

/**
 * Builder pattern for [Loc].
 *
 * @see Loc
 */
class LocBuilder {
    companion object {
        /**
         * @return a [LocBuilder] from [other].
         */
        fun from(other: Loc): LocBuilder {
            return other.asBuilder()
        }
    }

    private var col: UInt? = null
    private var line: UInt? = null
    private var len: UInt? = null

    /**
     * Sets `this.line` to [line].
     *
     * @return the new modified LocBuilder.
     */
    fun line(line: UInt): LocBuilder {
        return apply { this.line = line }
    }

    /**
     * Sets `this.col` to [col].
     *
     * @return the new modified LocBuilder.
     */
    fun col(col: UInt): LocBuilder {
        return apply { this.col = col }
    }

    /**
     * Sets `this.len` to [len].
     *
     * @return the new modified LocBuilder.
     */
    fun len(len: UInt): LocBuilder {
        return apply { this.len = len }
    }

    /**
     * Builds the [LocBuilder] into a [Loc] if possible.
     *
     * The following fields are required to be set for a build to be successful:
     *
     * - `col`
     * - `line`
     * - `len`
     *
     * @return a Loc if possible.
     *
     * @throws IllegalArgumentException when one of the fields mentioned above
     * aren’t set.
     */
    fun build(): Loc {
        require(col != null) { "A col must be provided" }
        require(line != null) { "A line must be provided" }
        require(len != null) { "A length must be provided" }

        return Loc(Module.curr(), col!!, line!!, len!!)
    }
}