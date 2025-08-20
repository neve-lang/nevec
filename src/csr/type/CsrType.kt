package csr.type

import csr.type.atom.CsrAtom

sealed class CsrType {
    data class Atom(val atom: CsrAtom) : CsrType()
    data object Unit : CsrType()
}