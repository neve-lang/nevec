package ast.hierarchy.decl

import ast.hierarchy.Ast
import ast.hierarchy.stmt.Stmt

/**
 * This sealed class denotes all kinds of supported Neve declarations so far.
 */
sealed class Decl : Ast {
    /**
     * Wrapper around a [Stmt].
     *
     * @see Stmt
     */
    data class OfStmt(val stmt: Stmt) : Decl()
}