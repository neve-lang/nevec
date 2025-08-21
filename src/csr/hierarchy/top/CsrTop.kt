package csr.hierarchy.top

import csr.hierarchy.expr.CsrExpr
import csr.type.CsrType

sealed class CsrTop {
    data class MainFun(val body: CsrExpr) : CsrTop()
    data class Fun(val name: String, val body: CsrExpr, val type: CsrType) : CsrTop()
}