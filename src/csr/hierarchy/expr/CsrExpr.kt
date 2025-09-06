package csr.hierarchy.expr

import csr.hierarchy.binop.CsrBinOp
import csr.hierarchy.lit.CsrLit
import csr.hierarchy.unop.CsrUnOp
import type.Type

sealed class CsrExpr {
    data class Print(val expr: CsrExpr) : CsrExpr()
    data class Parens(val expr: CsrExpr) : CsrExpr()

    /**
     * Represents a `let () = discarded` statement.
     *
     * [LetUnit] is useful for addressing cases where a body has more than one statement in the Neve source code.
     */
    data class LetUnit(val expr: CsrExpr) : CsrExpr()

    data class In(val left: CsrExpr, val right: CsrExpr) : CsrExpr()

    data class OfUnOp(val unOp: CsrUnOp, val type: Type) : CsrExpr()
    data class OfBinOp(val binOp: CsrBinOp, val type: Type) : CsrExpr()
    data class OfLit(val lit: CsrLit, val type: Type) : CsrExpr()

    fun type(): Type = when (this) {
        is Print -> expr.type()
        is Parens -> expr.type()
        is LetUnit -> expr.type()
        is In -> right.type()
        is OfUnOp -> type
        is OfBinOp -> type
        is OfLit -> type
    }
}