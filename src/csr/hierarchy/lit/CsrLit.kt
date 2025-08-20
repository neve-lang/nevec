package csr.hierarchy.lit

sealed class CsrLit {
    data class CsrInt(val value: Int) : CsrLit()
    data class CsrFloat(val value: Float) : CsrLit()
    data class CsrBool(val value: Boolean) : CsrLit()
    data class CsrStr(val value: String) : CsrLit()
    data object CsrNil : CsrLit()
}