package csr.type

import csr.type.applied.CsrApplied
import csr.type.atom.CsrAtom

sealed class CsrType {
    data class Atom(val atom: CsrAtom) : CsrType()
    data class Applied(val applied: CsrApplied) : CsrType()
    data object Unit : CsrType()
}