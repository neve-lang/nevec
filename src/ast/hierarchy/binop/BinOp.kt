package ast.hierarchy.binop

import ast.hierarchy.Ast
import ast.hierarchy.Spanned
import ast.hierarchy.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import file.span.Loc
import type.Type

/**
 * This sealed class denotes all kinds of binary operations supported in Neve so far.
 * They are separated as such to facilitate later compilation stages.
 * Note that the [Operator] enum is not separated as such.
 *
 * @see Operator
 */
sealed class BinOp : Ast, Wrap<Expr>, Spanned, Typed {
    data class Bitwise(
        val left: Expr, val operator: Operator, val right: Expr, val loc: Loc, val type: Type = Type.unresolved()
    ) : BinOp()

    data class Arith(
        val left: Expr, val operator: Operator, val right: Expr, val loc: Loc, val type: Type = Type.unresolved()
    ) : BinOp()

    data class Comp(
        val left: Expr, val operator: Operator, val right: Expr, val loc: Loc, val type: Type = Type.unresolved()
    ) : BinOp()

    data class Concat(val loc: Loc, val type: Type = Type.unresolved(), val left: Expr, val right: Expr) : BinOp()

    override fun wrap() = Expr.BinOpExpr(this)

    override fun loc() = when (this) {
        is Bitwise -> loc
        is Arith -> loc
        is Comp -> loc
        is Concat -> loc
    }

    override fun type() = when (this) {
        is Bitwise -> type
        is Arith -> type
        is Comp -> type
        is Concat -> type
    }

    fun operands() = when (this) {
        is Bitwise -> Pair(left, right)
        is Arith -> Pair(left, right)
        is Comp -> Pair(left, right)
        is Concat -> Pair(left, right)
    }
}