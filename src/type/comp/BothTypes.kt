package type.comp

import type.Type
import type.gen.Applied
import type.gen.Free
import type.hinted.Hinted
import type.impl.RecessType
import type.poison.Poison
import type.prim.Prim

/**
 * Represents a **relationship between two [Types][Type]**.
 *
 * This class serves to simplify the process of performing operations on two types, i.e. comparing
 * their type kinds, their domains, determining whether they are identical...
 *
 * Works as a helper for [Unify][infer.unify.Unify].
 *
 * @see Type
 * @see infer.unify.Unify
 */
data class BothTypes(val a: Type, val b: Type) {
    /**
     * @return whether two types can be unified, i.e. if they same [TypeKind][type.kind.TypeKind], unless one or
     * both are [recessive types][RecessType].
     *
     * @see type.kind.TypeKind
     * @see type.poison.Poison
     * @see Free
     * @see Hinted
     * @see RecessType
     */
    fun canBeUnified(): Boolean {
        return when {
            eitherIsRecess() -> true
            else -> a.kind::class == b.kind::class
        }
    }

    /**
     * @return whether either [Type] of [a] or [b] is a [Free] type.
     *
     * @see Free
     */
    fun eitherIsFree(): Boolean {
        return a.isFree() || b.isFree()
    }

    /**
     * @return whether either [Type] of [a] or [b] is a [Hinted][type.hinted.Hinted] type.
     *
     * @see type.hinted.Hinted
     */
    fun eitherIsHinted(): Boolean {
        return a.isHinted() || b.isHinted()
    }

    /**
     * @return whether either [Type] of [a] or [b] is [a poisoned type][Type.isPoisoned].
     *
     * @see type.poison.Poison
     */
    fun eitherIsPoisoned(): Boolean {
        return a.isPoisoned() || b.isPoisoned()
    }

    /**
     * @return whether either [Type] of [a] or [b] is [an unresolved type][Type.isUnresolved].
     *
     * @see type.poison.Poison
     */
    fun eitherIsUnresolved(): Boolean {
        return a.isUnresolved() || b.isUnresolved()
    }

    /**
     * @return whether both [a] and [b] are the same, according to [Compare][type.impl.Compare]’s
     * [isSame][type.impl.Compare.isSame] method.
     *
     * @see type.impl.Compare.isSame
     */
    fun areSame(): Boolean {
        return a.isSame(b)
    }

    /**
     * @return whether both [a] and [b], as [Applied] types, have the same type argument count.
     * @throws IllegalArgumentException if [areApplied] is not `true`.
     *
     * @see Applied
     */
    fun haveSameArity(): Boolean {
        require(areApplied()) {
            "Cannot check `haveSameArity` on `BothTypes` that does not exclusively contain `Applied` types."
        }

        return (
                (a.itself() as Applied).argCount() ==
                (b.itself() as Applied).argCount()
        )
    }

    /**
     * @return whether both [a] and [b] have the same [PrimKind][type.prim.PrimKind].
     * @throws IllegalArgumentException if [arePrim] is not `true`.
     *
     * @see type.prim.PrimKind
     * @see Prim
     */
    fun haveSamePrimKind(): Boolean {
        require(arePrim()) {
            "Cannot check `haveSamePrimKind` on a `BothTypes` that does not contain `Prim` types."
        }

        return (
                (a.itself() as Prim).kind ==
                (b.itself() as Prim).kind
        )
    }

    /**
     * @return from either [a] or [b], the [Type] that is not a [Free] type.
     * If both types are [Free], [a] is returned.
     */
    fun pickUnifiable(): Type {
        return if (b.isFree())
            a
        else
            b
    }

    /**
     * @return from either [a] or [b], the [Type] that is not an [unresolved type][Type.isUnresolved].
     * If both types are [unresolved][type.poison.Poison.Unresolved], [a] is returned.
     */
    fun pickResolved(): Type {
        return if (b.isUnresolved())
            a
        else
            b
    }

    /**
     * @return from either [a] of [b], the [Type] that is a [Hinted][type.hinted.Hinted] type, as a
     * [Hinted][type.hinted.Hinted].
     *
     * If both types are hinted, [a] is returned.
     * @throws IllegalArgumentException if [eitherIsHinted] is `false`.
     */
    fun pickHinted(): Hinted {
        require(eitherIsHinted()) {
            "Cannot perform `pickHinted` on `BothTypes` with no `Hinted` types."
        }

        return if (a.isHinted())
            a.itself() as Hinted
        else
            b.itself() as Hinted
    }

    /**
     * Picks the “appropriate poison” for two **different types**, where **none is a
     * [recessive type][type.impl.RecessType].
     *
     * Unifying two different non-recessive types always results in a poisoned type, but we need to be careful—if
     * either of those types is an [Unknown][eitherIsUnknown], we need to return an
     * [Ignorable][type.poison.Poison.Ignorable] type.
     *
     * @return An [Ignorable][type.poison.Poison.Ignorable] type if [eitherIsUnknown], an
     * [Unknown][type.poison.Poison.Unknown] type otherwise.
     *
     * @see type.poison.Poison.Ignorable
     */
    fun pickPoison(): Type {
        return if (eitherIsUnknown())
            Type.poisoned(with = Poison.Ignorable)
        else
            Type.unknown()
    }

    /**
     * “Unwraps” both types by removing their [Hinted] wrapper, if there is one.
     *
     * @return A new [BothTypes] without any [Hinted] wrappers.
     */
    fun unwrapHints(): BothTypes {
        return BothTypes(
            unwrapHint(from = a),
            unwrapHint(from = b)
        )
    }

    private fun eitherIsRecess(): Boolean {
        return a.itself() is RecessType || b.itself() is RecessType
    }

    private fun eitherIsUnknown(): Boolean {
        return a.isUnknown() || b.isUnknown()
    }

    private fun areApplied(): Boolean {
        return a.isApplied() && b.isApplied()
    }

    private fun arePrim(): Boolean {
        return a.isPrim() && b.isPrim()
    }

    private fun unwrapHint(from: Type): Type {
        return if (from.isHinted())
            (from.itself() as Hinted).type
        else
            from
    }
}