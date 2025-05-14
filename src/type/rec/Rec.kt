package type.rec

import file.module.Module
import type.impl.NamedType
import type.kind.TypeKind
import type.impl.Wrappable
import type.gen.param.TypeParams
import type.impl.Compare
import type.rec.field.Fields

/**
 * Represents a **record type**.
 *
 * The Neve compiler considers every type to be a record type, even [primitives][type.prim.Prim] like `Int`.
 * This allows it to track fields for primitive types that **do have fields** elegantly.
 *
 * For example, the `List` and `Str` primitives have a `len` field, and by representing them as record types,
 * the compiler can easily track these fields.
 *
 * @property module The module the record type comes from.
 * @property name The name of the type.
 * @property fields The record type’s fields, if any.
 * @property params The record type’s type parameters, if any.
 *
 * @see Fields
 * @see TypeParams
 */
data class Rec(
    val module: Module, val name: String, val fields: Fields, val params: TypeParams
) : Wrappable, NamedType, Compare<Rec> {
    companion object {
        fun builder(): RecBuilder {
            return RecBuilder()
        }
    }

    override fun wrap(): TypeKind {
        return TypeKind.OfRec(this)
    }

    override fun named(): String {
        return name
    }

    override fun isSame(other: Rec): Boolean {
        // NOTE: name mangling hasn’t been implemented yet.
        return named() == other.named()
    }

    override fun toString(): String {
        return named()
    }
}