package ast.hierarchy

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
 *
 * @see parse.Parse.primary
 */
interface Wrap<Super> {
    fun wrap(): Super
}