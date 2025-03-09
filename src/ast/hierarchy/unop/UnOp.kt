package ast.hierarchy.unop

import ast.hierarchy.GetLoc
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import file.span.Loc

/**
 * This sealed class denotes all kinds of supported Neve unary operations so far.
 */
sealed class UnOp : Wrap<Expr>, GetLoc {
    data class Neg(val loc: Loc, val expr: Expr) : UnOp()
    data class Not(val loc: Loc, val expr: Expr) : UnOp()

    override fun wrap() = Expr.UnOpExpr(this)

    override fun loc() = when (this) {
        is Neg -> loc
        is Not -> loc
    }
}