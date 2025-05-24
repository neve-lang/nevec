package ir.structure.op

import ir.info.IrInfo
import ir.structure.consts.IrConst
import ir.structure.impl.Termful
import ir.structure.tac.Tac
import ir.term.TermLike

/**
 * Represents all kinds of possible IR operations in the Neve IR.
 *
 * @param T The kind of term the [Op] should apply to.  This allows us to distinguish between the
 * [Warm][ir.term.warm.Warm] IR and the [Cool][ir.term.cool.Cool] IR.
 */
sealed class Op<T : TermLike> : Termful<T> {
    /**
     * A return operation.
     *
     * @param term The term to be returned.
     */
    data class Ret<T : TermLike>(val term: T, val info: IrInfo) : Op<T>()

    /**
     * A print operation.
     *
     * @param term The term to be printed.
     */
    data class Print<T : TermLike>(val term: T, val info: IrInfo) : Op<T>()

    /**
     * A Three-Address Code operation.
     *
     * @param tac The operation itself.
     */
    data class OfTac<T : TermLike>(val tac: Tac<T>) : Op<T>()

    /**
     * A constant operation—a term being set to a constant.
     *
     * @param to The term that receives the result.
     * @param const The constant being given.
     */
    data class Const<T : TermLike>(val to: T, val const: IrConst, val info: IrInfo) : Op<T>()

    override fun term(): T = when (this) {
        is Ret -> term
        is Print -> term
        is Const -> to
        is OfTac -> tac.term()
    }

    override fun allTerms() = when (this) {
        is Ret -> listOf(term)
        is Print -> listOf(term)
        is Const -> listOf(to)
        is OfTac -> tac.allTerms()
    }

    override fun isDefinition() = when (this) {
        is Ret -> false
        is Print -> false
        is Const -> true
        is OfTac -> tac.isDefinition()
    }
}