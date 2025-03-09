package ast.hierarchy.binop

import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import file.span.Loc

/**
 * This sealed class denotes all kinds of binary operations supported in Neve so far.
 * They are separated as such to facilitate later compilation stages.
 * Note that the [Operator] enum is not separated as such.
 *
 * @see Operator
 */
sealed class BinOp : Wrap<Expr> {
    data class Bitwise(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) : BinOp()
    data class Arith(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) : BinOp()
    data class Comp(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) : BinOp()
    data class Concat(val loc: Loc, val left: Expr, val right: Expr) : BinOp()

    override fun wrap() = Expr.BinOpExpr(this)
}