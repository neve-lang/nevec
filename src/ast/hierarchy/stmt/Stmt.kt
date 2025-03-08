package ast.hierarchy.stmt

import ast.hierarchy.expr.Expr
import file.span.Loc

sealed class Stmt {
    data class Print(val loc: Loc, val expr: Expr) : Stmt()
    data class ExprStmt(val expr: Expr) : Stmt()
}