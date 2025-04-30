package infer.unify

import ast.hierarchy.expr.Expr
import infer.comp.BothTypes
import type.Type
import type.gen.Applied
import type.gen.arg.TypeArgs
import type.hinted.Hinted
import type.kind.TypeKind
import type.poison.Poison
import type.prim.Prim
import util.extension.map

/**
 * Performs type unification in Hindley-Milner parlance.  Works alongside [Infer][infer.Infer].
 *
 * @property a The left-hand side to be unified.
 * @property b The right-hand side to be unified.
 *
 * @see infer.Infer
 */
class Unify(private val a: Type, private val b: Type) {
    companion object {
        /**
         * Builds a [Unify] class from a [Pair] of [Expr].
         *
         * @param pair The [Pair] in question.
         *
         * @return A [Unify] class with the **types** of both members of the [Pair].
         */
        fun both(pair: Pair<Expr, Expr>): Unify {
            return pair.map(Expr::type).let {
                (a, b) -> Unify(a, b)
            }
        }

        /**
         * Builds a [Unify] class from a [BothTypes] class.
         *
         * @param types The [BothTypes] in question.
         *
         * @return A [Unify] class with the both members of the [BothTypes].
         */
        fun from(types: BothTypes): Unify {
            return Unify(types.a, types.b)
        }

        /**
         * Tries to recursively unify the list of types.
         *
         * @param types The list of types in question.
         *
         * @return the unified type if unification was successful, a [poisoned type][type.poison.Poison] otherwise.
         */
        fun all(types: List<Type>): Type {
            return if (types.size == 2)
                Unify(types[0], types[1]).infer()
            else
                Unify(all(types.dropLast(1)), types.last()).infer()
        }

        /**
         * Tries to unify two lists of [Types][Type] as a **zip** of both.
         *
         * @param a The first list of types.
         * @param b The second list of types.
         *
         * @return the list of unified types.  It may contain poisoned types if the unification was unsuccessful
         * in certain places.
         */
        fun together(a: List<Type>, b: List<Type>): List<Type> {
            return a.zip(b).map { (a, b) -> Unify(a, b).infer() }
        }
    }

    /**
     * Checks whether [a] is equal to [b], assuming both types are equal to any type in [types].
     *
     * In other words, it checks whether `a = b` and `forall s in some : s = a`.
     *
     * This method **does not** perform true unification.
     *
     * @return [a] if `true`, a [poisoned type][type.poison.Poison] otherwise.
     */
    fun assuming(vararg types: Type): Type {
        val contained = types.any { BothTypes(it, a).haveSameName() }

        return if (contained && both().haveSameName())
            a
        else
            Type.unknown()
    }

    /**
     * Checks if [a] and [b] are equal, and returns [what] if `true`.
     *
     * This method **does not** perform true unification.
     *
     * @param what The type to be accepted if [a] and [b] are equal.
     *
     * @return [what] if [a] and [b] are equal, a [poisoned type][type.poison.Poison] otherwise.
     */
    fun into(what: Type): Type {
        return if (both().haveSameName())
            what
        else
            Type.unknown()
    }

    /**
     * Tries to unify [a] and [b].
     *
     * @return The unified type if unification was successful, a [poisoned type][type.poison.Poison] otherwise.
     *
     * @see type.poison.Poison
     */
    fun infer(): Type {
        if (!both().canBeUnified()) {
            return Type.unknown()
        }

        if (both().eitherIsFree()) {
            return unifyFree()
        }

        if (both().eitherIsHinted()) {
            return unifyHinted()
        }

        if (both().eitherIsPoisoned()) {
            return unifyPoison()
        }

        return unifySame()
    }

    private fun unifySame(): Type {
        return when (a.kind) {
            is TypeKind.OfApplied -> unifyApplied(
                a.itself() as Applied,
                b.itself() as Applied
            )

            is TypeKind.OfPrim -> unifyPrim(
                a.itself() as Prim,
                b.itself() as Prim
            )

            is TypeKind.OfRec -> unifyRec()
            is TypeKind.OfPoison -> unifyPoison()

            is TypeKind.OfQuant -> throw IllegalArgumentException(
                "Quantified types in `Forall` nodes must be instantiated before unification."
            )

            else -> throw IllegalArgumentException("Unexpected `TypeKind`.")
        }
    }

    private fun unifyFree(): Type {
        return both().pickUnifiable()
    }

    private fun unifyHinted(): Type {
        val hinted = both().pickHinted()


        return from(both().unwrapHints()).infer().cured() ?: Type.poisoned(with = Poison.Hint(
            hinted.type,
            hinted.loc
        ))
    }

    private fun unifyApplied(a: Applied, b: Applied): Type {
        if (!both().haveSameArity()) {
            return Type.unknown()
        }

        val unified = Unify(a.type, b.type).infer()

        return unified.cured()?.let {
            Applied(
                TypeArgs.from(together(a.argsList(), b.argsList())),
                it
            ).covered()
        } ?: Type.unknown()
    }

    private fun unifyPrim(a: Prim, b: Prim): Type {
        return if (both().haveSamePrimKind())
            Prim(
                a.kind, Unify(a.type, b.type).infer()
            ).covered()
        else
            Type.unknown()
    }

    private fun unifyRec(): Type {
        return if (both().haveSameName())
            a
        else
            Type.unknown()
    }

    private fun unifyPoison(): Type {
        return Type.poisoned(with = Poison.Ignorable)
    }

    private fun both(): BothTypes {
        return BothTypes(a, b)
    }
}
