package type.hinted

import type.NamedType
import type.WrappedType
import type.kind.TypeKind

/**
 * Represents a type that was **explicitly hinted by the user**.
 *
 * This distinction useful for the semantic resolver and type checker, which allows
 * the compiler to provide more accurate error messages.
 *
 * When an [TypeKind.OfHinted] is confirmed to be valid, the type is extracted using [accept].
 *
 * @property type The actual type that was hinted by the user.
 */
data class Hinted(val type: TypeKind) : WrappedType, NamedType {
    /**
     * Accepts the hinted type as valid.
     *
     * @return the [type] that [Hinted] represents.
     */
    fun accept(): TypeKind {
        return type
    }

    override fun wrap(): TypeKind {
        return TypeKind.OfHinted(this)
    }

    override fun named(): String {
        return type.named()
    }
}