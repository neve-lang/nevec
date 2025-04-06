package check.analys

import ast.typeful.TypefulExpr
import ast.typeful.binop.TypefulBinOp
import ast.typeful.unop.TypefulUnOp
import chance.Chances
import visit.Visit

/**
 * The value analysis module.
 *
 * Currently on the works--I’m taking a tiny step back from the compiler because the whole “value analysis” problem
 * turned out to be incredibly more complex than I thought.
 */
object Analys /* : Visit<TypefulExpr, Chances?> */ {
    private fun visitUnOp(unOp: TypefulUnOp) = when (unOp) {
        is TypefulUnOp.Neg -> With(unOp).of<Chances.OfNum> {
            Chances.OfNum(it.chances.map { a -> -a })
        }

        is TypefulUnOp.Not -> With(unOp).of<Chances.OfBool> {
            Chances.OfBool(it.chances.map { a -> !a })
        }
    }
}