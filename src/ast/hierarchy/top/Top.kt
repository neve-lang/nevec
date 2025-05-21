package ast.hierarchy.top

import ast.hierarchy.Ast
import file.span.Loc

/**
 * Represents all kinds of **top-level** declarations supported in Neve.
 *
 * Notice how they are different from **[simple declarations][ast.hierarchy.decl.Decl]**—simple declarations have
 * an [OfStmt][ast.hierarchy.decl.Decl.OfStmt] variant which, if used for top-level declarations as well, would
 * allow top-level statements—something Neve wants to avoid.
 */
sealed class Top : Ast {
    /**
     * A function declaration node.
     *
     * Right now, we do not support arguments.  We are simply accommodating for the main function.
     */
    data class Fun(val name: String, val loc: Loc) : Top()
}
