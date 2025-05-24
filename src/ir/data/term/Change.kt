package ir.data.term

import ir.structure.op.Op
import ir.term.TermLike

/**
 * Refers to a specific change to a term’s **statistics**, such as a change in the number of usages, or a new
 * term definition.
 *
 * @see TermData
 * @see Stats
 */
sealed class Change<T : TermLike> {
    companion object {
        /**
         * @return A list of [Pair], each pair encoding the following information:
         *
         * - The [first][Pair.first] element of the pair denotes the list of terms the [Change] should be applied to.
         * - The [second][Pair.second] element of the pair is the [Change] itself.
         */
        fun <T : TermLike> deriveFrom(op: Op<T>): List<Pair<Change<T>, List<T>>> {
            val (receiver, rest) = termsOf(op)
            val useChanges = Uses(new = op) to rest

            return if (op.isDefinition())
                listOf(Def(op) to receiver) + useChanges
            else
                listOf(Uses(new = op) to receiver) + useChanges
        }

        private fun <T : TermLike> termsOf(op: Op<T>): Pair<List<T>, List<T>> {
            return listOf(op.term()) to op.allTerms().drop(1)
        }
    }

    /**
     * Represents a change in a term’s usage count.
     *
     * @property by The integer representing the change—addition will be performed.
     */
    data class Uses<T : TermLike>(val new: Op<T>) : Change<T>() {
        override fun applyTo(previous: Stats<T>): Stats<T> {
            return Stats(previous.def, uses = previous.uses + new)
        }
    }

    /**
     * Represents a change in a term’s definition.
     *
     * @property op The IR [Op] that will replace the previous one.
     */
    data class Def<T : TermLike>(val op: Op<T>?) : Change<T>() {
        override fun applyTo(previous: Stats<T>): Stats<T> {
            return Stats(def = op, previous.uses)
        }
    }

    /**
     * Applies a `this` [Change] to a [Stats].
     *
     * @param previous The [Stats] that will receive the change.
     *
     * @return A new [Stats] with the change applied.
     */
    abstract fun applyTo(previous: Stats<T>): Stats<T>
}