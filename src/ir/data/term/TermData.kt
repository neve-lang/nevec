package ir.data.term

import ir.data.change.TermChange
import ir.structure.block.Block
import ir.term.TermLike
import ir.term.id.TermId

/**
 * Stores a map of term definitions, which allow for easy lookup.
 *
 * # Motivation
 *
 * Certain optimizations, like **constant folding**, require knowledge about certain terms.  Folding the following
 * IR operation:
 *
 * ```
 * t2 = t0 + t1
 * ```
 *
 * Is only possible **if both terms of the addition**—in this case, `t0` and `t1`—are constant values.
 *
 * This is where [TermData] comes in: it creates a [Map] of term IDs and their IR [statistics][Stats], for each
 * fully-lowered IR function.  A new, updated [TermData] should be provided whenever a term’s statistics are changed.
 *
 * [TermData] may appear in [Compose][ir.structure.compose.Compose] data classes, but this is only to aid in building
 * the final [IrFun][ir.structure.fun.IrFun].
 */
data class TermData<T : TermLike>(val themselves: Map<TermId, Stats<T>>) {
    companion object {
        /**
         * @return A new [TermData] with an empty [Map].
         */
        fun <T : TermLike> new(): TermData<T> {
            return TermData(emptyMap())
        }

        /**
         * @return A new [TermData] that is built from examining each [Op][ir.structure.op.Op] of a generated list of
         * IR blocks.
         */
        fun <T : TermLike> from(blocks: List<Block<T>>): TermData<T> {
            val ops = blocks.flatMap { it.ops }
            val changes = ops.flatMap { TermChange.deriveFrom(it) }

            return updateAll(changes)
        }

        private fun <T : TermLike> updateAll(changes: List<TermChange<T>>): TermData<T> {
            return if (changes.isEmpty())
                new()
            else
                updateAll(changes.drop(1)).update(changes.first())
        }
    }

    /**
     * @return The [Stats] associated with the given [T] term.
     *
     * If no [Stats] are associated with that term, `null` is returned instead.
     */
    fun statsOf(term: T): Stats<T>? {
        return themselves[term.id()]
    }

    /**
     * Applies a [TermChange] to `this`—i.e. it applies the [TermChange] to all the [Stats] of all the terms the
     * change applies on.
     *
     * If a term is not registered, a default [Stats] is created and the change is applied to that default member.
     *
     * @return A new [TermData] with the changes applied.
     */
    fun update(termChange: TermChange<T>): TermData<T> {
        return this +
                (
                    termChange.terms().map(TermLike::id) zip
                            applyToTerms(termChange)
                ).toMap().let(::TermData)
    }

    /**
     * @return A new [TermData] with both maps of both operands merged into one.
     */
    operator fun plus(other: TermData<T>): TermData<T> {
        return TermData(
            themselves + other.themselves
        )
    }

    private fun applyToTerms(termChange: TermChange<T>): List<Stats<T>> {
        return statsOfAll(termChange.terms()).map { termChange.applyTo(it) }
    }

    private fun statsOfAll(terms: List<T>): List<Stats<T>> {
        return terms.map { themselves[it.id()] }.map {
            it ?: Stats()
        }
    }
}