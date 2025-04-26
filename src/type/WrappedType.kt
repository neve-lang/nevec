package type

import type.kind.TypeKind

/**
 * Provides a `wrap` function that wraps the type data class into its corresponding [TypeKind] wrapper.
 */
interface WrappedType {
    /**
     * @return the implementor wrapper into its corresponding [TypeKind] wrapper.
     */
    fun wrap(): TypeKind
}