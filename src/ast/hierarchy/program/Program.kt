package ast.hierarchy.program

import ast.hierarchy.Ast
import ast.hierarchy.decl.Decl

/**
 * This AST node denotes a Neve program.
 */
data class Program(val decls: List<Decl>) : Ast {
    companion object {
        fun empty() = Program(emptyList())
    }
}