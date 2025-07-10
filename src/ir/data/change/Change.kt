package ir.data.change

import ir.data.`fun`.FunData
import ir.structure.consts.IrConst
import ir.structure.op.Op
import ir.term.TermLike

/*
 * Refers to a specific change in a function’s data, usually due to a modification applied by an optimization pass.
 */
sealed class Change<T : TermLike> {
    companion object {
        /**
         * @return A list of [Changes][Change] that could be derived from the given IR [Op][Op].  The list may be
         * empty.
         */
        fun <T : TermLike> deriveFrom(op: Op<T>): List<Change<T>> {
            return when (op) {
                is Op.Const -> deriveFromConst(op) + termChanges(op)
                else -> termChanges(op)
            }
        }

        private fun <T : TermLike> deriveFromConst(op: Op.Const<T>): List<Change<T>> {
            return Const(
                const = op.const,
                term = op.term()
            ).let(::listOf)
        }

        private fun <T : TermLike> termChanges(op: Op<T>): List<Change<T>> {
            return TermChange.deriveFrom(op).map { it.wrap() }
        }
    }

    /**
     * Inserts an additional term in the [FunData]’s map of [IrConsts][IrConst].
     *
     * @property const The [IrConst] to be modified.
     * @property term The new additional term to be associated with [const].
     */
    data class Const<T : TermLike>(val const: IrConst, val term: T) : Change<T>() {
        override fun applyTo(previous: FunData<T>): FunData<T> {
            val previousTerms = previous.termsUsing(const)

            return previous + FunData(
                constDefMap = mapOf(const to previousTerms + term)
            )
        }
    }

    /**
     * Removes the specified term from the [FunData]’s map of [IrConsts][IrConst].
     *
     * @property const The [IrConst] to be modified.
     * @property term The term to be removed.
     */
    data class Unconst<T : TermLike>(val const: IrConst, val term: T) : Change<T>() {
        override fun applyTo(previous: FunData<T>): FunData<T> {
            val previousTerms = previous.termsUsing(const)

            return previous.copy(
                constDefMap = previous.constDefMap + mapOf(const to previousTerms - term)
            )
        }
    }

    /**
     * Wrapper variant around [TermChange].
     *
     * @property termChange The [TermChange] being wrapped.
     */
    data class OfTerm<T : TermLike>(val termChange: TermChange<T>) : Change<T>() {
        override fun applyTo(previous: FunData<T>): FunData<T> {
            return previous + FunData(
                termData = previous.termData.update(termChange)
            )
        }
    }

    /**
     * Applies `this` [TermChange] to a [FunData].
     *
     * @param previous The [FunData] that will experience the change.
     *
     * @return A new [FunData] with the change applied.
     */
    abstract fun applyTo(previous: FunData<T>): FunData<T>
}