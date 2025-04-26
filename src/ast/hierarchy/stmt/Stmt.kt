package ast.hierarchy.stmt

import ast.hierarchy.Ast
import ast.info.Spanned
import ast.hierarchy.Wrap
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import file.span.Loc

/**
 * This sealed class denotes all kinds of supported Neve statements so far.
 */
sealed class Stmt : Ast, Wrap<Decl>, Spanned {
    /**
     * A print statement.
     *
     * NOTE: Print statements in Neve are only temporary.  They will be removed in future
     * versions.
     */
    data class Print(val expr: Expr, val loc: Loc) : Stmt()

    /**
     * Wrapper around [Expr] nodes.
     *
     * @see Expr
     */
    data class OfExpr(val expr: Expr) : Stmt()

    override fun wrap(): Decl.OfStmt {
        return Decl.OfStmt(this)
    }

    override fun loc() = when (this) {
        is Print -> loc
        is OfExpr -> expr.loc()
    }
}