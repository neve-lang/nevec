package ast.hierarchy

/**
 * An AST node that can be wrapped into its supertype in the AST hierarchy.
 *
 * I.e., a [Lit][ast.hierarchy.lit.Lit] node may be wrapped into an [Expr][ast.hierarchy.expr.Expr] node.
 *
 * An example usage of the [Wrap] interface may be:
 *
 * ```
 * val decl: Decl = when (kind()) {
 *   TokKind.CONST -> constDecl()
 *   // ...
 *   else -> stmt().wrap() // returns a Decl.OfStmt
 * }
 * ```
 */
interface Wrap<Super> {
    fun wrap(): Super
}