package ast.hierarchy.unop

import ast.hierarchy.Ast
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import ast.info.Info
import ast.info.impl.Infoful
import file.span.Loc

/**
 * This sealed class denotes all kinds of supported Neve unary operations so far.
 */
sealed class UnOp : Ast, Wrap<Expr>, Infoful {
    /**
     * A unary negation node.
     *
     * @param op The [Loc] of the operator token.
     */
    data class Neg(val expr: Expr, val info: Info, val op: Loc) : UnOp()

    /**
     * A unary boolean flip node.
     *
     * @param op The [Loc] of the operator token.
     */
    data class Not(val expr: Expr, val info: Info, val op: Loc) : UnOp()

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

    override fun info() = when (this) {
        is Neg -> info
        is Not -> info
    }

    override fun update(new: Info) = when (this) {
        is Neg -> Neg(expr, new, op)
        is Not -> Not(expr, new, op)
    }
}