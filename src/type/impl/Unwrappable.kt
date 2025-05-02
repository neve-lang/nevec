package type.impl

/**
 * Provides an [itself] method that returns a type of [To].
 *
 * Used within [TypeKind][type.kind.TypeKind]’s variants to simplify the process of extracting the **original type**
 * from the wrapper variant.
 *
 * @see type.kind.TypeKind
 */
interface Unwrappable<To: Wrappable> {
    /**
     * Unwraps the implementor into its [To] original type.
     */
    fun itself(): To
}