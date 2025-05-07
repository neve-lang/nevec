package ast.hierarchy.expr

import ast.hierarchy.Ast
import ast.info.impl.Spanned
import ast.info.impl.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.binop.BinOp
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.unop.UnOp
import ast.info.Info
import ast.info.impl.Infoful
import file.span.Loc

/**
 * This sealed class denotes all kinds of supported Neve expressions so far.
 */
sealed class Expr : Ast, Wrap<Stmt>, Infoful {
    /**
     * A parenthesized expression.
     */
    data class Parens(val expr: Expr, val info: Info) : Expr()

    /**
     * A show expression — an expression being converted to a string.
     *
     * This node is temporary, although it does exist in the IR — we’re keeping it in the
     * AST for now to simplify string interpolation.
     */
    data class Show(val expr: Expr, val info: Info) : Expr()

    /* These nodes will be re-implemented later.
    /**
     * A symbol access node.
     */
    data class Access(val name: String, val info: Info) : Expr()

    /**
     * Different from an [Access]; the distinction only appears in later compilation stages, after semantic resolving.
     * It is used to facilitate resolving when building the IR, as [Consts][ast.hierarchy.decl.Decl.Consts] aren't
     * stored in the same place as regular symbols are.
     *
     * NOTE: Right now, only [AccessConst]s are supported.  Other [Access] nodes will not make it past the semantic
     * resolving phase, as there are no regular symbols to access — they will all become [AccessConst].
     *
     * @see Access
     */
    data class AccessConst(val name: String, val info: Info) : Expr()
     */

    /**
     * Wrapper around an [UnOp] node.
     *
     * @see UnOp
     */
    data class OfUnOp(val unOp: UnOp) : Expr()

    /**
     * Wrapper around a [BinOp] node.
     *
     * @see BinOp
     */
    data class OfBinOp(val binOp: BinOp) : Expr()

    /**
     * Wrapper around a [Lit] node.
     *
     * @see Lit
     */
    data class OfLit(val lit: Lit) : Expr()

    /**
     * Wrapper around an [Interpol] node.
     *
     * @see Interpol
     */
    data class OfInterpol(val interpol: Interpol) : Expr()

    /**
     * Dummy node: this node shouldn't ever appear in a valid AST.  It is returned when the parser encounters an
     * unexpected token in the context of an expression.
     *
     * It still carries an [Info], because it needs to adhere to the [Wrap], [Spanned], and [Typed] interfaces,
     * albeit [type] is *always* `Type.unresolved()`.
     *
     * @see parse.Parse.primary
     */
    data class Empty(val info: Info) : Expr()

    companion object {
        /**
         * @return An [Empty] node with its `loc` as [Loc.new].
         */
        fun empty(): Empty {
            return Empty(Info.at(Loc.new()))
        }
    }

    override fun wrap(): Stmt {
        return Stmt.OfExpr(this)
    }

    override fun loc() = when (this) {
        is Parens -> info.loc()
        is Show -> info.loc()
        // is Access -> info.loc()
        // is AccessConst -> info.loc()
        is Empty -> info.loc()
        is OfUnOp -> unOp.loc()
        is OfBinOp -> binOp.loc()
        is OfLit -> lit.loc()
        is OfInterpol -> interpol.loc()
    }

    override fun type() = when (this) {
        is Parens -> info.type()
        is Show -> info.type()
        // is Access -> info.type()
        // is AccessConst -> info.type()
        is Empty -> info.type()
        is OfUnOp -> unOp.type()
        is OfBinOp -> binOp.type()
        is OfLit -> lit.type()
        is OfInterpol -> interpol.type()
    }

    override fun meta() = when (this) {
        is Parens -> info.meta()
        is Show -> info.meta()
        // is Access -> info.meta()
        // is AccessConst -> info.meta()
        is Empty -> info.meta()
        is OfUnOp -> unOp.meta()
        is OfBinOp -> binOp.meta()
        is OfLit -> lit.meta()
        is OfInterpol -> interpol.meta()
    }

    override fun update(new: Info): Infoful = when (this) {
        is Parens -> Parens(expr, new)
        is Show -> Show(expr, new)
        is Empty -> Empty(new)
        is OfUnOp -> unOp.update(new)
        is OfBinOp -> binOp.update(new)
        is OfLit -> lit.update(new)
        is OfInterpol -> interpol.update(new)
    }
}