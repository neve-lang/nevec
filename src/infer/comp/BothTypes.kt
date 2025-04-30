package infer.comp

import type.Type
import type.gen.Applied
import type.gen.Free
import type.kind.TypeKind
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
     * both are [free type variables][Free].
     *
     * @see type.kind.TypeKind
     * @see Free
     */
    fun canBeUnified(): Boolean {
        return when {
            a.kind is TypeKind.OfFree || b.kind is TypeKind.OfFree -> true
            else -> a.kind::class == b.kind::class
        }
    }

    /**
     * @return whether both [a] and [b] have the same name.
     *
     * NOTE: Name mangling hasn’t been implemented yet, but [haveSameName] will be supposed to compare the
     * mangled names of both types.  Right now, it just compares the simple name.
     */
    fun haveSameName(): Boolean {
        return a.named() == b.named()
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
     * @return whether both [a] and [b] are [unknown types][Type.unknown].
     *
     * @see type.poison.Poison
     */
    fun areUnknown(): Boolean {
        return a.isUnknown() || b.isUnknown()
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
     * @return whether either [Type] of [a] or [b] is [an unknown type][Type.unknown].
     *
     * @see type.poison.Poison
     */
    fun eitherIsUnknown(): Boolean {
        return a.isUnknown() || b.isUnknown()
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

    private fun areApplied(): Boolean {
        return a.isApplied() && b.isApplied()
    }

    private fun arePrim(): Boolean {
        return a.isPrim() && b.isPrim()
    }
}