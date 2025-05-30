package ir.data.change

import ir.data.`fun`.FunData
import ir.structure.consts.IrConst
import ir.structure.op.Op
import ir.term.TermLike

/**
 * Analogous to [Change][ir.data.term.Change], but applies to [FunData] instead.
 *
 * @see ir.data.term.Change
 */
sealed class FunChange<T : TermLike> {
    companion object {
        /**
         * @return A list of [FunChanges][FunChange] that could be derived from the given IR [Op][Op].  The list may be
         * empty.
         */
        fun <T : TermLike> deriveFrom(op: Op<T>): List<FunChange<T>> {
            return when (op) {
                is Op.Const -> listOf(Const(const = op.const, term = op.term()))
                else -> emptyList()
            }
        }
    }

    /**
     * Represents a change in the [FunData]’s map of [IrConsts][IrConst].
     */
    data class Const<T : TermLike>(val const: IrConst, val term: T) : FunChange<T>() {
        override fun applyTo(data: FunData<T>): FunData<T> {
            val terms = data.termsUsing(const) ?: emptyList()

            return data + FunData(
                mapOf(const to terms + term)
            )
        }
    }

    /**
     * @return A new [FunData] with `this` [FunChange] applied to the given [FunData].
     */
    abstract fun applyTo(data: FunData<T>): FunData<T>
}