package type

import domain.Domain
import type.impl.Compare
import type.impl.NamedType
import type.impl.Unwrappable
import type.impl.Wrappable
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
data class Type(val kind: TypeKind, val domain: Domain = Domain.Undefined) : Unwrappable<Wrappable>, NamedType,
    Compare<Type> {
    companion object {
        /**
         * @return A [Type] with [kind] of `OfPoison(Poison.Unresolved)` and [domain] of [Domain.Undefined].
         */
        fun unresolved(): Type {
            return Type(TypeKind.unresolved())
        }

        /**
         * @param name The name of the type, as it appears in the code.
         *
         * @return A [Type] with [kind] of `OfPoison(Poison.Undefined(name))` and [domain] of [Domain.Undefined].
         *
         * @see Poison.Undefined
         */
        fun undefined(name: String): Type {
            return Type(TypeKind.undefined(name))
        }

        /**
         * @return A [Type] with [kind] of `OfPoison(Poison.Unknown)` and [domain] of [Domain.Undefined].
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
     * @return whether [kind] is a [poisoned type][Poison] with [Poison.Unresolved] poison.
     *
     * @see Poison
     */
    fun isUnresolved(): Boolean {
        return kind is TypeKind.OfUnresolved
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

    /**
     * @return whether [kind] is of [TypeKind.OfPrim]
     */
    fun isPrim(): Boolean {
        return kind is TypeKind.OfPrim
    }

    /**
     * Tries to “cure” the poisoned type by returning `null` if the [Type] is indeed poisoned.
     *
     * @return `this` if the [Type] is not poisoned, `null` otherwise.
     *
     * @see isPoisoned
     * @see Poison
     */
    fun cured(): Type? {
        return if (isPoisoned())
            null
        else
            this
    }

    /**
     * @return Whether the implementor type and [to] have the same [Domain][domain.Domain], and
     * [have the same name][isSame].
     *
     * The reason why we provide another comparison method, is that there are **two kinds of type comparisons in Neve**:
     *
     * - Comparing whether the name is the same, sometimes referred to as **shallow comparison**.  This is because Neve
     *   mainly uses nominal typing.  [isSame] accomplishes this.
     * - Comparing whether the **domain** is the same.  If we define two types, `A` and `B`, both as
     *   `Int where self == 10`,
     *   then both types should be considered to have the same domain, but not the same identity.
     *
     * This comparison method is the most used one.
     *
     * @see domain.Domain
     */
    fun isIdentical(to: Type): Boolean {
        return isSame(to) && domain == to.domain
    }

    override fun itself(): Wrappable {
        return kind.unwrapped()
    }

    override fun named(): String {
        return kind.named()
    }

    override fun isSame(other: Type): Boolean {
        return kind.isSame(other.kind)
    }
}