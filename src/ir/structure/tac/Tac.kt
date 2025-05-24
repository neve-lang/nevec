package ir.structure.tac

import ir.info.IrInfo
import ir.structure.impl.Termful
import ir.structure.op.Op
import ir.term.TermLike

/**
 * Represents a simple **Three-Address Code** operation.
 *
 * Note that the Neve compiler uses a **Single Static Assignment** IR.
 *
 * @param T The kind of term the [Tac] should apply to.  This allows us to distinguish between the
 * [Warm][ir.term.warm.Warm] IR and the [Cool][ir.term.cool.Cool] IR.
 */
sealed class Tac<T : TermLike> : Termful<T> {
    /**
     * A negate operation.
     *
     * @param to The term that receives the result.
     * @param term The term that receives the operation.
     */
    data class Neg<T : TermLike>(val to: T, val term: T, val info: IrInfo) : Tac<T>()

    /**
     * A boolean `not` operation.
     *
     * @param to The term that receives the result.
     * @param term The term that receives the operation.
     */
    data class Not<T : TermLike>(val to: T, val term: T, val info: IrInfo) : Tac<T>()

    /**
     * A show operation.
     *
     * @param to The term that receives the result.
     * @param term The term that receives the operation.
     */
    data class Show<T : TermLike>(val to: T, val term: T, val info: IrInfo) : Tac<T>()

    /**
     * An addition operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Add<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A subtraction operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Sub<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A multiplication operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Mul<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A division operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Div<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A bitwise left shift operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Shl<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A bitwise right shift operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Shr<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A bitwise and operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class BitAnd<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A bitwise or operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class BitOr<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A bitwise xor operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class BitXor<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A “not equals” operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Neq<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * An “equals” operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Eq<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A “greater than” operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Gt<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A “greater or equal than” operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Gte<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A “less than” operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Lt<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A “less than or equal” operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Lte<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A string concatenation operation.
     *
     * @param to The term that receives the result.
     * @param left The left operand.
     * @param right The right operand.
     */
    data class Concat<T : TermLike>(val to: T, val left: T, val right: T, val info: IrInfo) : Tac<T>()

    /**
     * A “table set” operation.
     *
     * @param to The term that receives the result—the table.
     * @param key The term that represents the key.
     * @param value The value to be given.
     */
    data class TableSet<T : TermLike>(val to: T, val key: T, val value: T, val info: IrInfo) : Tac<T>()

    /**
     * @return A new [Tac] wrapped around an [Op.OfTac].
     */
    fun wrap(): Op.OfTac<T> {
        return Op.OfTac(this)
    }

    override fun term() = when (this) {
        is Neg -> to
        is Not -> to
        is Show -> to
        is Add -> to
        is Sub -> to
        is Mul -> to
        is Div -> to
        is Shl -> to
        is Shr -> to
        is BitAnd -> to
        is BitOr -> to
        is BitXor -> to
        is Neq -> to
        is Eq -> to
        is Gt -> to
        is Gte -> to
        is Lt -> to
        is Lte -> to
        is Concat -> to
        is TableSet -> to
    }

    override fun allTerms() = when (this) {
        is Neg -> listOf(to, term)
        is Not -> listOf(to, term)
        is Show -> listOf(to, term)
        is Add -> listOf(to, left, right)
        is Sub -> listOf(to, left, right)
        is Mul -> listOf(to, left, right)
        is Div -> listOf(to, left, right)
        is Shl -> listOf(to, left, right)
        is Shr -> listOf(to, left, right)
        is BitAnd -> listOf(to, left, right)
        is BitOr -> listOf(to, left, right)
        is BitXor -> listOf(to, left, right)
        is Neq -> listOf(to, left, right)
        is Eq -> listOf(to, left, right)
        is Gt -> listOf(to, left, right)
        is Gte -> listOf(to, left, right)
        is Lt -> listOf(to, left, right)
        is Lte -> listOf(to, left, right)
        is Concat -> listOf(to, left, right)
        is TableSet -> listOf(to, key, value)
    }

    override fun isDefinition() = when (this) {
        is TableSet -> false
        else -> true
    }
}