package meta

import meta.asserts.MetaAssert

/**
 * Encapsulates both [meta assertions][MetaAssert] and `meta annotations` into a same data class.
 */
data class Meta(val asserts: MutableList<MetaAssert<*>>) {
    companion object {
        /**
         * @return A [Meta] data class with empty lists.
         */
        fun empty(): Meta {
            return Meta(mutableListOf())
        }
    }

    /**
     * Mutates the current [Meta] object and adds [assert] to its list of [asserts].
     *
     * @param assert The assert in question.
     */
    fun add(assert: MetaAssert<*>) {
        asserts.add(assert)
    }
}