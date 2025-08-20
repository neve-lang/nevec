package csr.hierarchy.top

import csr.hierarchy.expr.CsrExpr
import csr.type.CsrType

sealed class CsrTop {
    data class CsrFun(val name: String, val body: CsrExpr, val type: CsrType) : CsrTop()
}