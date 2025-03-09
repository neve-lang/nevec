package ast.hierarchy.lit

import ast.hierarchy.expr.Expr
import file.span.Loc
import type.Type

/**
 * This sealed class denotes all supported expression literals in Neve so far.
 */
sealed class Lit {
    data class IntLit(val loc: Loc, val type: Type, val value: Int) : Lit()
    data class FloatLit(val loc: Loc, val type: Type, val value: Float) : Lit()
    data class BoolLit(val loc: Loc, val type: Type, val value: Boolean) : Lit()
    data class StrLit(val loc: Loc, val type: Type, val value: String) : Lit()
    data class NilLit(val loc: Loc) : Lit()
    data class TableLit(val loc: Loc, val keys: List<Expr>, val vals: List<Expr>) : Lit()
}