package csr.hierarchy.expr

import csr.hierarchy.binop.CsrBinOp
import csr.hierarchy.lit.CsrLit
import csr.hierarchy.unop.CsrUnOp
import type.Type

sealed class CsrExpr {
    data class Parens(val expr: CsrExpr) : CsrExpr()
    data class OfUnOp(val unOp: CsrUnOp, val type: Type) : CsrExpr()
    data class OfBinOp(val binOp: CsrBinOp, val type: Type) : CsrExpr()
    data class OfLit(val lit: CsrLit, val type: Type) : CsrExpr()

    fun type(): Type = when (this) {
        is Parens -> expr.type()
        is OfUnOp -> type
        is OfBinOp -> type
        is OfLit -> type
    }
}