package ast.hierarchy.unop

import ast.hierarchy.expr.Expr
import file.span.Loc

sealed class UnOp {
    data class Neg(val loc: Loc, val expr: Expr) : UnOp()
    data class Not(val loc: Loc, val expr: Expr) : UnOp()
}