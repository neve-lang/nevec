package check.infer

import ast.hierarchy.expr.Expr
import type.Type
import visit.Visit

object Infer : Visit<Expr, Type> {
    override fun visit(what: Expr) = TODO()
        /*
        is Expr.UnOpExpr -> visitUnOp(what.unOp)
        is Expr.BinOpExpr -> visitBinOp(what.binOp)
        is Expr.LitExpr -> visitLit(what.lit)
        is Expr.InterpolExpr -> visitInterpol(what.interpol)
        is Expr.Show -> visitShow(what)
        is Expr.AccessConst -> visitAccessConst(what)
        is Expr.Access -> visitAccess(what)
        is Expr.Parens -> visitParens(what)

        is Expr.Empty -> visitEmpty()
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitNeg(unOp)
        is UnOp.Not -> visitNot(unOp)
    }

    private fun visitNeg(unOp: UnOp.Neg): Type {
        val type = visit(unOp.expr)

        // NOTE: once we can, replace this with idea sharing checks
        Analys.visit()
    }
         */
}