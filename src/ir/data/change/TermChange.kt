package ir.data.change

import ir.data.term.Stats
import ir.structure.op.Op
import ir.term.TermLike

/**
 * Similar to [Change], but refers to a specific change in a **term’s** data.
 *
 * For example, a constant-folding pass may update a term’s definition.
 *
 * @see ir.data.term.TermData
 * @see ir.data.fun.FunData
 * @see Stats
 */
sealed class TermChange<T : TermLike> {
    companion object {
        /**
         * @return A list of [TermChange] that could be “derived” from the given [Op].
         *
         * This function in reality returns a list of [Uses] changes and a [Def] change, if the [op] given
         * [is a definition][Op.isDefinition].
         */
        fun <T : TermLike> deriveFrom(op: Op<T>): List<TermChange<T>> {
            val (receiver, rest) = termsOf(op)
            val useChanges = Uses(
                terms = rest,
                newUse = op
            )

            return if (op.isDefinition())
                listOf(Def(term = receiver, op)) + useChanges
            else
                listOf(Uses(terms = listOf(receiver), newUse = op)) + useChanges
        }

        private fun <T : TermLike> termsOf(op: Op<T>): Pair<T, List<T>> {
            return op.term() to op.allTerms().drop(1)
        }
    }

    /**
     * Represents a change in a set of terms’ usage IR operations.
     *
     * @property terms The terms whose usages should be modified.
     * @property newUse The IR [Op] where the terms were used.
     */
    data class Uses<T : TermLike>(val terms: List<T>, val newUse: Op<T>) : TermChange<T>() {
        override fun applyTo(previous: Stats<T>): Stats<T> {
            return Stats(previous.def, uses = previous.uses + newUse)
        }

        override fun terms(): List<T> {
            return terms
        }
    }

    /**
     * Represents a change that removes a specific [Op] in a term’s list of usages.
     *
     * @property terms The terms whose usages should be updated.
     * @property oldUse The IR [Op] that should be removed from the uses.
     */
    data class Unuse<T : TermLike>(val terms: List<T>, val oldUse: Op<T>) : TermChange<T>() {
        override fun applyTo(previous: Stats<T>): Stats<T> {
            return previous
                .uses
                .filter { it != oldUse }
                .let {
                    Stats(previous.def, it)
                }
        }

        override fun terms(): List<T> {
            return terms
        }
    }

    /**
     * Represents a change in a term’s definition.
     *
     * This [TermChange] can always only apply to a single term.
     *
     * @property term The term whose definition should be updated.
     * @property op The IR [Op] that will replace the previous one.
     */
    data class Def<T : TermLike>(val term: T, val op: Op<T>?) : TermChange<T>() {
        override fun applyTo(previous: Stats<T>): Stats<T> {
            return Stats(def = op, previous.uses)
        }

        override fun terms(): List<T> {
            return listOf(term)
        }
    }

    /**
     * Applies `this` [TermChange] to a term’s [Stats].
     *
     * @param previous The [Stats] that will experience the change.
     *
     * @return A new [Stats] with the change applied.
     */
    abstract fun applyTo(previous: Stats<T>): Stats<T>

    /**
     * @return A [List] of all the terms to which this [Change] should apply.
     */
    abstract fun terms(): List<T>

    /**
     * @return `this` [TermChange] wrapped around a [Change.OfTerm].
     */
    fun wrap(): Change<T> {
        return Change.OfTerm(this)
    }
}