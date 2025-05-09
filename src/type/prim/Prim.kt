package type.prim

import type.impl.NamedType
import type.Type
import type.impl.Compare
import type.kind.TypeKind
import type.impl.Wrappable

/**
 * Represents a primitive type.
 *
 * The Neve compiler distinguishes [Prims][Prim] from regular [Rec][type.rec.Rec] types because primitive types are
 * given a special [domain representation][domain.Domain] representation.
 *
 * @param kind The primitive kind that the [Prim] represents.
 * @param type The type itself; usually a [Rec][type.rec.Rec] type, because the Neve compiler still treats primitive
 * types as record types.  This is because types like `Str` still have fields.
 *
 * @see PrimKind
 * @see domain.Domain
 */
data class Prim(val kind: PrimKind, val type: Type) : Wrappable, NamedType, Compare<Prim> {
    override fun wrap(): TypeKind {
        return TypeKind.OfPrim(this)
    }

    override fun named(): String {
        return type.named()
    }

    override fun isSame(other: Prim): Boolean {
        return kind == other.kind && type.isSame(other.type)
    }
}

/**
 * Helper for wrapping a [TypeKind] into a [Prim] with the given [kind].
 *
 * @param kind the kind of [Prim] to be associated with [this].
 *
 * @return A [Prim] with [this] and [kind].
 */
fun Type.into(kind: PrimKind) = Prim(kind, this).wrap()