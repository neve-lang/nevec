package domain

/**
 * In the Neve compiler, a **domain** refers to an abstract representation of an expression’s possible values.
 *
 * [Prim][type.prim.Prim] types are given an explicit compiler representation of their possible values,
 * whereas record types are given an [OfNonPrim] representation.
 *
 * @see type.prim.Prim
 */
sealed class Domain {
    /**
     * An undefined domain.
     *
     * It is **not** the same as an [OfNonPrim] or [OfNil] domain.  [Undefined] domains are used alongside
     * [poisoned types][type.poison.Poison] or [free type variables][type.gen.Free].
     *
     * @see type.poison.Poison
     */
    data object Undefined : Domain()
}