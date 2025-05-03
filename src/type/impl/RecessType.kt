package type.impl

/**
 * Marker interface that denotes **recessive types**.
 *
 * A **recessive type** is defined as a type that behaves in a special way during [type unification][infer.unify.Unify].
 *
 * Examples of recessive types include [free types][type.gen.Free], [hinted types][type.hinted.Hinted], and
 * [poisoned types][type.poison.Poison].
 *
 * @see type.gen.Free
 * @see type.hinted.Hinted
 * @see type.poison.Poison
 */
interface RecessType