package ast.hierarchy.unop

import ast.hierarchy.Ast
import ast.hierarchy.Spanned
import ast.hierarchy.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import file.span.Loc
import type.Type

/**
 * This sealed class denotes all kinds of supported Neve unary operations so far.
 */
sealed class UnOp : Ast, Wrap<Expr>, Spanned, Typed {
    data class Neg(val expr: Expr, val loc: Loc, val type: Type = Type.unresolved()) : UnOp()
    data class Not(val expr: Expr, val loc: Loc, val type: Type = Type.unresolved()) : UnOp()

    override fun wrap() = Expr.UnOpExpr(this)

    override fun loc() = when (this) {
        is Neg -> loc
        is Not -> loc
    }

    override fun type() = when (this) {
        is Neg -> type
        is Not -> type
    }
}