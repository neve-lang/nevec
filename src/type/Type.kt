package type

import domain.Domain
import type.kind.TypeKind

/**
 * Represents a type in the Neve compiler.
 *
 * Unlike [TypeKind], this representation includes a [Domain], which serves to represent
 * each individual type’s **domain of possible values**.
 *
 * [Types][Type] may be unique to every expression and symbol, whereas [TypeKind] represent a
 * type with no domain representation attached to them.
 *
 * It is worth noting, however, that the [type table][type.table.TypeTable] does not store
 * named types as [TypeKinds][TypeKind], but rather as [Type], because refinement types
 * need to be registered too.
 *
 * @property kind The underlying type’s [TypeKind].
 * @property domain The type’s domain of possible values.
 *
 * @see TypeKind
 * @see type.table.TypeTable
 * @see Domain
 */
data class Type(val kind: TypeKind, val domain: Domain) : NamedType {
    companion object {
        /**
         * @return A [Type] with [kind] of `OfPoison(Poison.UNRESOLVED)` and [domain] of [Domain.Undefined].
         */
        fun unresolved(): Type {
            return Type(TypeKind.unresolved(), Domain.Undefined)
        }

        /**
         * @return A [Type] with [kind] of `OfPoison(Poison.UNKNOWN)` and [domain] of [Domain.Undefined].
         */
        fun unknown(): Type {
            return Type(TypeKind.unknown(), Domain.Undefined)
        }
    }

    override fun named(): String {
        return kind.named()
    }
}