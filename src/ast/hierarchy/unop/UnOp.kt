package ast.hierarchy.unop

import ast.hierarchy.Ast
import ast.info.Spanned
import ast.info.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import ast.info.Info
import file.span.Loc
import type.kind.TypeKind

/**
 * This sealed class denotes all kinds of supported Neve unary operations so far.
 */
sealed class UnOp : Ast, Wrap<Expr>, Spanned, Typed {
    /**
     * A unary negation node.
     */
    data class Neg(val expr: Expr, val info: Info) : UnOp()

    /**
     * A unary boolean flip node.
     */
    data class Not(val expr: Expr, val info: Info) : UnOp()

    override fun wrap(): Expr.OfUnOp {
        return Expr.OfUnOp(this)
    }

    override fun loc() = when (this) {
        is Neg -> info.loc()
        is Not -> info.loc()
    }

    override fun type() = when (this) {
        is Neg -> info.type()
        is Not -> info.type()
    }
}