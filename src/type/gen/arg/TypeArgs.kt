package type.gen.arg

import type.Type

/**
 * Groups together a list of a **generic type argument**, i.e. types applied to types parameters of a generic type.
 *
 * @property args The type arguments themselves.
 *
 * @see type.gen.Applied
 * @see type.gen.param.TypeParams
 */
data class TypeArgs(val args: List<Type>)