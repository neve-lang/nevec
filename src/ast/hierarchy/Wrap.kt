package ast.hierarchy

import ast.hierarchy.decl.Decl

/**
 * An AST node that can be wrapped into its supertype in the AST hierarchy.
 *
 * I.e., a [ast.hierarchy.lit.Lit] node may be wrapped into an [ast.hierarchy.expr.Expr] node.
 *
 * # Example
 *
 * ```
 * val decl: Decl = when (kind()) {
 *   TokKind.CONST -> constDecl()
 *   // ...
 *   else -> stmt().wrap()
 * }
 * ```
 */
interface Wrap<Super> {
    fun wrap(): Super
}