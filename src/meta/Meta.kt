package meta

import meta.asserts.MetaAssert

/**
 * Encapsulates both [meta assertions][MetaAssert] and `meta annotations` into a same data class.
 */
data class Meta(val asserts: List<MetaAssert<*>>) {
    companion object {
        /**
         * @return A [Meta] data class with empty lists.
         */
        fun empty(): Meta {
            return Meta(emptyList())
        }
    }
}