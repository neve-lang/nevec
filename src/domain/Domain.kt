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
     * The domain of `nil` values.
     *
     * The domain of `nil` values is defined to be always the same.  We represent it as an empty data object.
      */
    data object OfNil : Domain()

    /**
     * A non-primitive domain.
     *
     * This domain is used alongside user-defined records.  Their fields, however, may have a domain representation.
     */
    data object OfNonPrim : Domain()

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