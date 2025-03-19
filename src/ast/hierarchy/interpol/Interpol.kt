package ast.hierarchy.interpol

import ast.hierarchy.Ast
import ast.hierarchy.Spanned
import ast.hierarchy.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import file.span.Loc
import type.Type

/**
 * A union-like sealed class to denote the type of each component of a string interpolation expression.
 * Each "variant" is used as a node of a linked-list.
 *
 * # Example
 *
 * Consider the following interpolation:
 *
 * ```
 * "Hello, #{"world!"}  This message took #{2.5 * 3.} to reach Earth."
 * ```
 *
 * We would roughly build the following linked-list:
 *
 * ```
 * Some("Hello, ", expr = StrLit("world!")) -> Some("  This message took ", expr = ...) -> End(" to reach Earth.")
 * ```
 *
 */
sealed class Interpol : Ast, Wrap<Expr>, Spanned, Typed {
    /**
     * A "part" of a string interpolation that contains an [expr] and a [next] component, working similarly to
     * the node of a linked-list.
     */
    data class Some(
        val string: String, val expr: Expr, val next: Interpol, val loc: Loc, val type: Type = Type.unresolved()
    ) : Interpol()

    /**
     * The end of a string interpolation--the last node in the linked-list.  It does not contain an expression.
     *
     * In the case of the following interpolation:
     *
     * ```
     * "Time elapsed: #{start - clock}"
     * ```
     *
     * `End`'s [string] will simply be an empty one.
     */
    data class End(val string: String, val loc: Loc, val type: Type = Type.unresolved()) : Interpol()

    override fun wrap() = Expr.InterpolExpr(this)

    override fun loc() = when (this) {
        is Some -> loc
        is End -> loc
    }

    override fun type() = when (this) {
        is Some -> type
        is End -> type
    }
}