package type.gen

import type.NamedType
import type.kind.TypeKind
import type.WrappedType
import type.gen.param.TypeParams

/**
 * Represents a type with a list of generic type parameters.
 *
 * @property params The type parameters in question.
 * @property type The type to which the [params] belong.
 *
 * @see TypeParams
 */
data class Gen(val params: TypeParams, val type: TypeKind) : WrappedType, NamedType {
    override fun wrap(): TypeKind {
        return TypeKind.OfGen(this)
    }

    override fun named(): String {
        return type.named()
    }
}