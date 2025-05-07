package ast.hierarchy.interpol

import ast.hierarchy.Ast
import ast.info.impl.Spanned
import ast.info.impl.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import ast.info.Info
import ast.info.impl.Infoful
import meta.Meta

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
sealed class Interpol : Ast, Wrap<Expr>, Infoful {
    /**
     * A "part" of a string interpolation that contains an [expr] and a [next] component, working similarly to
     * the node of a linked-list.
     */
    data class Some(
        val string: String, val expr: Expr, val next: Interpol, val info: Info
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
    data class End(val string: String, val info: Info) : Interpol()

    override fun wrap(): Expr.OfInterpol {
        return Expr.OfInterpol(this)
    }

    override fun loc() = when (this) {
        is Some -> info.loc()
        is End -> info.loc()
    }

    override fun type() = when (this) {
        is Some -> info.type()
        is End -> info.type()
    }

    override fun meta() = when (this) {
        is Some -> info.meta()
        is End -> info.meta()
    }

    override fun update(new: Info) = when (this) {
        is Some -> Some(string, expr, next, new)
        is End -> End(string, new)
    }
}