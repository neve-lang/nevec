package type.impl

import type.Type
import type.kind.TypeKind

/**
 * Provides a `wrap` function that wraps the type data class into its corresponding [TypeKind] wrapper.
 */
interface Wrappable {
    /**
     * @return the implementor wrapped into its corresponding [TypeKind] wrapper.
     */
    fun wrap(): TypeKind

    /**
     * Works as a sort of “double [wrap].”
     *
     * @return the implementor wrapped into a [Type].
     */
    fun covered(): Type {
        return Type(wrap())
    }
}