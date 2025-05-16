package ast.hierarchy.binop

import ast.hierarchy.Ast
import ast.info.impl.Spanned
import ast.info.impl.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.binop.operator.*
import ast.hierarchy.expr.Expr
import ast.info.Info
import ast.info.impl.Infoful
import file.span.Loc
import meta.Meta

/**
 * This sealed class denotes all kinds of binary operations supported in Neve so far.
 * They are separated as such to facilitate later compilation stages.
 *
 * Note that [Operator] is not separated as such.
 *
 * @see Operator
 */
sealed class BinOp : Ast, Wrap<Expr>, Infoful {
    /**
     * A bitwise operation node.
     *
     * @param op The [Loc] of the operator token.
     *
     * @see BitwiseOperator
     */
    data class Bitwise(
        val left: Expr, val operator: BitwiseOperator, val right: Expr, val info: Info, val op: Loc
    ) : BinOp()

    /**
     * An arithmetic operation node.
     *
     * @param op The [Loc] of the operator token.
     *
     * @see ArithOperator
     */
    data class Arith(
        val left: Expr, val operator: ArithOperator, val right: Expr, val info: Info, val op: Loc
    ) : BinOp()

    /**
     * A comparison node.
     *
     * @param op The [Loc] of the operator token.
     *
     * @see CompOperator
     */
    data class Comp(
        val left: Expr, val operator: CompOperator, val right: Expr, val info: Info, val op: Loc
    ) : BinOp()

    /**
     * A concatenation node.
     *
     * @param op The [Loc] of the operator token.
     *
     * @see ConcatOperator
     */
    data class Concat(
        val left: Expr, val operator: ConcatOperator, val right: Expr, val info: Info, val op: Loc
    ) : BinOp()

    /**
     * @return a [Pair] of `left` and `right` operands.
     */
    fun operands() = when (this) {
        is Bitwise -> Pair(left, right)
        is Arith -> Pair(left, right)
        is Comp -> Pair(left, right)
        is Concat -> Pair(left, right)
    }

    override fun wrap(): Expr {
        return Expr.OfBinOp(this)
    }

    override fun loc() = when (this) {
        is Bitwise -> info.loc()
        is Arith -> info.loc()
        is Comp -> info.loc()
        is Concat -> info.loc()
    }

    override fun type() = when (this) {
        is Bitwise -> info.type()
        is Arith -> info.type()
        is Comp -> info.type()
        is Concat -> info.type()
    }

    override fun meta() = when (this) {
        is Bitwise -> info.meta()
        is Arith -> info.meta()
        is Comp -> info.meta()
        is Concat -> info.meta()
    }

    override fun info() = when (this) {
        is Bitwise -> info
        is Arith -> info
        is Comp -> info
        is Concat -> info
    }

    override fun update(new: Info) = when (this) {
        is Bitwise -> Bitwise(left, operator, right, new, op)
        is Arith -> Arith(left, operator, right, new, op)
        is Comp -> Comp(left, operator, right, new, op)
        is Concat -> Concat(left, operator, right, new, op)
    }
}