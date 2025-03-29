package check.analys

import ast.hierarchy.expr.Expr
import ast.hierarchy.unop.UnOp
import ast.typeful.TypefulExpr
import ast.typeful.unop.TypefulUnOp
import type.chance.Chances
import visit.Visit

/**
 * The value analysis module.
 */
object Analys : Visit<TypefulExpr, Chances?> {
    override fun visit(what: TypefulExpr): Chances? {
        return when (what) {
            is TypefulExpr.UnOpExpr -> visitUnOp(what.unOp)
        }
    }

    private fun visitUnOp(unOp: TypefulUnOp): Chances? {
        return when (unOp) {
            is TypefulUnOp.Neg -> With(unOp.operand.chances).of<Chances.OfNum> {
                Chances.OfNum(it.chances.map { a -> -a })
            }

            is UnOp.Not -> With(type).of<Chances.OfBool> {
                Chances.OfBool(it.chances.map { a -> !a })
            }
        }
    }
}