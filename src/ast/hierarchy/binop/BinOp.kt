package ast.hierarchy.binop

import ast.hierarchy.Ast
import ast.info.impl.Spanned
import ast.info.impl.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.binop.operator.*
import ast.hierarchy.expr.Expr
import ast.info.Info
import ast.info.impl.Infoful
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
     * @see BitwiseOperator
     */
    data class Bitwise(
        val left: Expr, val operator: BitwiseOperator, val right: Expr, val info: Info
    ) : BinOp()

    /**
     * An arithmetic operation node.
     *
     * @see ArithOperator
     */
    data class Arith(
        val left: Expr, val operator: ArithOperator, val right: Expr, val info: Info
    ) : BinOp()

    /**
     * A comparison node.
     *
     * @see CompOperator
     */
    data class Comp(
        val left: Expr, val operator: CompOperator, val right: Expr, val info: Info
    ) : BinOp()

    /**
     * A concatenation node.
     */
    data class Concat(
        val left: Expr, val operator: ConcatOperator, val right: Expr, val info: Info
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

    override fun wrap(): Expr {
        return Expr.OfBinOp(this)
    }
}