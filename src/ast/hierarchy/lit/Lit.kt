package ast.hierarchy.lit

import ast.hierarchy.Ast
import ast.hierarchy.Spanned
import ast.hierarchy.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import file.span.Loc
import type.Type

/**
 * This sealed class denotes all supported expression literals in Neve so far.
 */
sealed class Lit : Ast, Wrap<Expr>, Spanned, Typed {
    data class IntLit(val value: Int, val loc: Loc, val type: Type = Type.unresolved()) : Lit()
    data class FloatLit(val value: Float, val loc: Loc, val type: Type = Type.unresolved()) : Lit()
    data class BoolLit(val value: Boolean, val loc: Loc, val type: Type = Type.unresolved()) : Lit()
    data class StrLit(val value: String, val loc: Loc, val type: Type = Type.unresolved()) : Lit()
    data class NilLit(val loc: Loc, val type: Type = Type.unresolved()) : Lit()
    data class TableLit(val keys: List<Expr>, val vals: List<Expr>, val loc: Loc, val type: Type = Type.unresolved()) : Lit()

    override fun wrap() = Expr.LitExpr(this)

    override fun loc() = when (this) {
        is IntLit -> loc
        is FloatLit -> loc
        is BoolLit -> loc
        is StrLit -> loc
        is NilLit -> loc
        is TableLit -> loc
    }

    override fun type() = when (this) {
        is IntLit -> type
        is FloatLit -> type
        is BoolLit -> type
        is StrLit -> type
        is TableLit -> type
        is NilLit -> type
    }
}