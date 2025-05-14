package type.gen.forall

import type.Type

/**
 * Represents a **forall quantifier** or a **polytype** in Hindley-Milner parlance.
 *
 * @property names The names of each [quantified type][type.gen.Quant], mapped by their IDs.
 * @property type The type the [Forall] quantifier is being applied to.
 */
data class Forall(val names: Map<Int, String>, val type: Type)