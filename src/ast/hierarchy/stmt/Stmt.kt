package ast.hierarchy.stmt

import ast.hierarchy.expr.Expr
import file.span.Loc

/**
 * This sealed class denotes all kinds of supported Neve statements so far.
 */
sealed class Stmt {
    data class Print(val loc: Loc, val expr: Expr) : Stmt()
    data class ExprStmt(val expr: Expr) : Stmt()
}