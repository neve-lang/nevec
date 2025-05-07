package ast.hierarchy.unop

import ast.hierarchy.Ast
import ast.info.impl.Spanned
import ast.info.impl.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import ast.info.Info
import ast.info.impl.Infoful

/**
 * This sealed class denotes all kinds of supported Neve unary operations so far.
 */
sealed class UnOp : Ast, Wrap<Expr>, Infoful {
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

    override fun meta() = when (this) {
        is Neg -> info.meta()
        is Not -> info.meta()
    }

    override fun update(new: Info) = when (this) {
        is Neg -> Neg(expr, new)
        is Not -> Not(expr, new)
    }
}