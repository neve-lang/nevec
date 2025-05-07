package ast.hierarchy.lit

import ast.hierarchy.Ast
import ast.info.impl.Spanned
import ast.info.impl.Typed
import ast.hierarchy.Wrap
import ast.hierarchy.expr.Expr
import ast.info.Info
import ast.info.impl.Infoful
import meta.Meta

/**
 * This sealed class denotes all supported expression literals in Neve so far.
 */
sealed class Lit : Ast, Wrap<Expr>, Infoful {
    /**
     * An integer literal.
     */
    data class IntLit(val value: Int, val info: Info) : Lit()

    /**
     * A float literal.
     */
    data class FloatLit(val value: Float, val info: Info) : Lit()

    /**
     * A boolean literal.
     */
    data class BoolLit(val value: Boolean, val info: Info) : Lit()

    /**
     * A string literal.
     */
    data class StrLit(val value: String, val info: Info) : Lit()

    /**
     * A nil literal.
     */
    data class NilLit(val info: Info) : Lit()

    /**
     * A table literal.
     */
    data class TableLit(val keys: List<Expr>, val vals: List<Expr>, val info: Info) : Lit()

    override fun wrap(): Expr.OfLit {
        return Expr.OfLit(this)
    }

    override fun loc() = when (this) {
        is IntLit -> info.loc()
        is FloatLit -> info.loc()
        is BoolLit -> info.loc()
        is StrLit -> info.loc()
        is NilLit -> info.loc()
        is TableLit -> info.loc()
    }

    override fun type() = when (this) {
        is IntLit -> info.type()
        is FloatLit -> info.type()
        is BoolLit -> info.type()
        is StrLit -> info.type()
        is TableLit -> info.type()
        is NilLit -> info.type()
    }

    override fun meta() = when (this) {
        is IntLit -> info.meta()
        is FloatLit -> info.meta()
        is BoolLit -> info.meta()
        is StrLit -> info.meta()
        is TableLit -> info.meta()
        is NilLit -> info.meta()
    }

    override fun update(new: Info) = when (this) {
        is IntLit -> IntLit(value, new)
        is FloatLit -> FloatLit(value, new)
        is BoolLit -> BoolLit(value, new)
        is StrLit -> StrLit(value, new)
        is TableLit -> TableLit(keys, vals, new)
        is NilLit -> NilLit(new)
    }
}