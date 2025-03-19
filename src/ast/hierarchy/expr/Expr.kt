package ast.hierarchy.expr

import ast.hierarchy.Ast
import ast.hierarchy.Spanned
import ast.hierarchy.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.binop.BinOp
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.unop.UnOp
import file.span.Loc
import tok.Tok
import type.Type

/**
 * This sealed class denotes all kinds of supported Neve expressions so far.
 */
sealed class Expr : Ast, Wrap<Stmt>, Spanned, Typed {
    data class Parens(val expr: Expr, val loc: Loc) : Expr()
    data class Show(val expr: Expr, val loc: Loc, val type: Type = Type.unresolved()) : Expr()
    data class Access(val tok: Tok, val type: Type = Type.unresolved()) : Expr()

    /**
     * Different from an [Access]; the distinction only appears in later compilation stages, after semantic resolving.
     * It is used to facilitate resolving when building the IR.
     */
    data class AccessConst(val loc: Loc, val type: Type, val name: String) : Expr()

    data class UnOpExpr(val unOp: UnOp) : Expr()
    data class BinOpExpr(val binOp: BinOp) : Expr()
    data class LitExpr(val lit: Lit) : Expr()
    data class InterpolExpr(val interpol: Interpol) : Expr()

    data class Empty(val loc: Loc, val type: Type = Type.unresolved()) : Expr()

    override fun wrap() = Stmt.ExprStmt(this)

    override fun loc() = when (this) {
        is Parens -> loc
        is Show -> loc
        is Access -> tok.loc
        is AccessConst -> loc
        is Empty -> loc
        is UnOpExpr -> unOp.loc()
        is BinOpExpr -> binOp.loc()
        is LitExpr -> lit.loc()
        is InterpolExpr -> interpol.loc()
    }

    override fun type(): Type = when (this) {
        is Parens -> expr.type()
        is Show -> type
        is Access -> type
        is AccessConst -> type
        is Empty -> type
        is UnOpExpr -> unOp.type()
        is BinOpExpr -> binOp.type()
        is LitExpr -> lit.type()
        is InterpolExpr -> interpol.type()
    }
}