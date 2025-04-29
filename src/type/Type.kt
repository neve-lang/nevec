package type

import domain.Domain
import type.kind.TypeKind
import type.poison.Poison

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
data class Type(val kind: TypeKind, val domain: Domain = Domain.Undefined) : NamedType {
    companion object {
        /**
         * @return A [Type] with [kind] of `OfPoison(Poison.UNRESOLVED)` and [domain] of [Domain.Undefined].
         */
        fun unresolved(): Type {
            return Type(TypeKind.unresolved())
        }

        /**
         * @return A [Type] with [kind] of `OfPoison(Poison.UNKNOWN)` and [domain] of [Domain.Undefined].
         */
        fun unknown(): Type {
            return Type(TypeKind.unknown())
        }

        /**
         * @param with The poison in question.
         *
         * @return a [Type] with [kind] of [TypeKind.OfPoison], with [with] as the poison.
         */
        fun poisoned(with: Poison): Type {
            return Type(TypeKind.OfPoison(with))
        }
    }

    /**
     * @return whether [kind] is a [poisoned type][Poison] with [Poison.UNKNOWN] poison.
     *
     * @see Poison
     */
    fun isUnknown(): Boolean {
        return kind is TypeKind.OfPoison && kind.poison == Poison.UNKNOWN
    }

    /**
     * @return whether [kind] is of [TypeKind.OfFree].
     */
    fun isFree(): Boolean {
        return kind is TypeKind.OfFree
    }

    /**
     * @return whether [kind] is of [TypeKind.OfHinted].
     */
    fun isHinted(): Boolean {
        return kind is TypeKind.OfHinted
    }

    /**
     * @return whether [kind] is of [TypeKind.OfPoison].
     */
    fun isPoisoned(): Boolean {
        return kind is TypeKind.OfPoison
    }

    /**
     * @return whether [kind] is of [TypeKind.OfApplied].
     */
    fun isApplied(): Boolean {
        return kind is TypeKind.OfApplied
    }

    override fun named(): String {
        return kind.named()
    }
}