package type.refine

import ast.hierarchy.expr.Expr
import type.Type

class Refine(val predicate: Expr)

fun Type.refined(predicate: Expr): Type.RefineType {
    val refine = Refine(predicate)
    return Type.RefineType(refine, this)
}