package ast.hierarchy.stmt

import ast.hierarchy.Ast
import ast.hierarchy.Spanned
import ast.hierarchy.Wrap
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import file.span.Loc

/**
 * This sealed class denotes all kinds of supported Neve statements so far.
 */
sealed class Stmt : Ast, Wrap<Decl>, Spanned {
    data class Print(val loc: Loc, val expr: Expr) : Stmt()
    data class ExprStmt(val expr: Expr) : Stmt()

    override fun wrap() = Decl.StmtDecl(this)

    override fun loc() = when (this) {
        is Print -> loc
        is ExprStmt -> expr.loc()
    }
}