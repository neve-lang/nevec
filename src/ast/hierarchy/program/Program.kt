package ast.hierarchy.program

import ast.hierarchy.Ast
import ast.hierarchy.decl.Decl
import ast.hierarchy.top.Top

/**
 * Denotes a Neve program file.
 *
 * A Neve program file is made of a list of declarations at top level.
 *
 * NOTE: This data class may be renamed to File once we implement a module system.
 *
 * @property decls The list of declarations the program file is made of.
 *
 * @see Decl
 */
data class Program(val decls: List<Top>) : Ast {
    companion object {
        /**
         * @return A [Program] with an [emptyList] of [decls].
         */
        fun empty(): Program {
            return Program(emptyList())
        }
    }
}