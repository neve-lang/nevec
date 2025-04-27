package type.gen

import type.NamedType
import type.Type
import type.kind.TypeKind
import type.WrappedType
import type.gen.param.TypeParams
import type.gen.arg.TypeArgs

/**
 * Represents a type with a list of generic type arguments applied to it.
 *
 * @property args The type parameters in question.
 * @property type The type to which the [args] need to be applied to.
 *
 * @see TypeParams
 */
data class Applied(val args: List<TypeArgs>, val type: Type) : WrappedType, NamedType {
    override fun wrap(): TypeKind {
        return TypeKind.OfApplied(this)
    }

    override fun named(): String {
        return type.named()
    }
}