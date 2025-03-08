package type.prim

import type.Type
import type.WrappedType

data class Prim(val kind: PrimKind, val type: Type) : WrappedType {
    override fun wrap() = Type.PrimType(this)
}

fun Type.into(kind: PrimKind) = Prim(kind, this).wrap()