package type.gen.param

/**
 * Wrapper around the list of generic type parameters a generic type takes.
 *
 * @property params The [TypeParams][TypeParam] in question, mapped by their index.  Their index is made **explicit** to simplify
 * each param to each type argument when applying type arguments to a generic type.
 *
 * @see TypeParam
 */
data class TypeParams(val params: Map<Int, TypeParam>) {
    companion object {
        /**
         * @return a [TypeParams] wrapper with an [emptyMap].
         */
        fun none(): TypeParams {
            return TypeParams(emptyMap())
        }

        /**
         * Creates a list of [TypeParams][TypeParam] from [names] and builds a [Map] from it.
         *
         * @param names The names of the type parameters as [Strings][String].
         *
         * @return A [TypeParams] with the [Map] built as described above.
         */
        fun from(vararg names: String): TypeParams {
            return TypeParams(
                names.map(::TypeParam).withIndex().associate { it.index to it.value }
            )
        }
    }
}