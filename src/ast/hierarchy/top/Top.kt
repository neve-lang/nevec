package ast.hierarchy.top

import ast.hierarchy.Ast
import ast.hierarchy.decl.Decl
import file.span.Loc

/**
 * Represents all kinds of **top-level** declarations supported in Neve.
 *
 * Notice how they are different from **[simple declarations][Decl]**—simple declarations have
 * an [OfStmt][Decl.OfStmt] variant which, if used for top-level declarations as well, would
 * allow top-level statements—something Neve wants to avoid.
 */
sealed class Top : Ast {
    /**
     * A function declaration node.
     *
     * Right now, we do not support arguments.  We are simply accommodating for the main function.
     */
    data class Fun(val name: String, val decls: List<Decl>, val loc: Loc) : Top()

    /**
     * A dummy top-level declaration.
     *
     * Occurs in the AST when a top-level declaration was expected, but none was found.
     */
    data class Empty(val loc: Loc) : Top()
}