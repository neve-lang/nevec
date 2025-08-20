package csr.type.applied

import csr.type.CsrType

data class CsrApplied(val callee: CsrType, val args: List<CsrType>)