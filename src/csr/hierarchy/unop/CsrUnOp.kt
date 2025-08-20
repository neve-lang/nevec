package csr.hierarchy.unop

import csr.hierarchy.expr.CsrExpr

sealed class CsrUnOp {
    data class Neg(val operand: CsrExpr) : CsrUnOp()
    data class Not(val operand: CsrExpr) : CsrUnOp()
}