package ast.hierarchy.decl

import ast.hierarchy.stmt.Stmt
import file.span.Loc
import type.Type

/**
 * This sealed class denotes all kinds of supported Neve declarations so far.
 */
sealed class Decl {
    /**
     * A single constant declaration in [Consts].
     *
     * ```
     * const Math
     *   Pi = 3.14 # <-- this would be a Const.
     * end
     * ```
     */
    data class Const(val loc: Loc, val type: Type) : Decl()

    /**
     * A set of [Const] declarations.
     *
     * ```
     * const Math
     *   Pi = 3.14
     * end
     * ```
     */
    data class Consts(val consts: List<Const>) : Decl()
    data class StmtDecl(val stmt: Stmt) : Decl()
}