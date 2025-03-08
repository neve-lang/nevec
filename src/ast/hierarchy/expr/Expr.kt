package ast.hierarchy.expr

import ast.hierarchy.lit.Lit
import ast.hierarchy.unop.UnOp
import ast.hierarchy.binop.BinOp
import file.span.Loc
import tok.Tok
import type.Type

sealed class Expr {
    data class Parens(val loc: Loc, val expr: Expr) : Expr()
    data class Show(val loc: Loc, val expr: Expr) : Expr()
    data class Access(val type: Type, val tok: Tok) : Expr()
    data class AccessConst(val loc: Loc, val type: Type, val name: String) : Expr()
    data class UnOpExpr(val unOp: UnOp) : Expr()
    data class BinOpExpr(val binOp: BinOp) : Expr()
    data class LitExpr(val lit: Lit) : Expr()
}