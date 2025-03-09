package ast.hierarchy

import ast.hierarchy.decl.Decl

/**
 * This AST node denotes a Neve program.
 */
data class Program(val decls: List<Decl>)