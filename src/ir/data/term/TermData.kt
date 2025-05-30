package ir.data.term

import ir.data.change.Change
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
            val changes = ops.flatMap { Change.deriveFrom(it) }

            return updateAll(changes)
        }

        private fun <T : TermLike> updateAll(changes: List<Pair<Change<T>, List<T>>>): TermData<T> {
            return if (changes.isEmpty())
                new()
            else
                changes.first().let {
                    (change, terms) -> updateAll(changes.drop(1)).update(change, terms)
                }
        }
    }

    /**
     * Applies a same [Change] to the given list of terms.
     *
     * If a term is not registered, a default [Stats] is created and the change is applied to that default member.
     *
     * @return A new [TermData] with the changes applied.
     */
    fun update(change: Change<T>, terms: List<T>): TermData<T> {
        return this + terms.let {
            listOfTerms -> listOfTerms.map(TermLike::id) zip
                statsOf(listOfTerms).map { change.applyTo(it) }
        }.toMap().let(::TermData)
    }

    /**
     * @return A new [TermData] with both maps of both operands merged into one.
     */
    operator fun plus(other: TermData<T>): TermData<T> {
        return TermData(
            themselves + other.themselves
        )
    }

    private fun statsOf(terms: List<T>): List<Stats<T>> {
        return terms.map { themselves[it.id()] }.map {
            it ?: Stats()
        }
    }
}