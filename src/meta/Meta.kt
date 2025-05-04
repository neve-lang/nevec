package meta

import meta.asserts.MetaAssert
import meta.target.Target

/**
 * Encapsulates both [meta assertions][MetaAssert] and `meta annotations` into a same data class.
 */
data class Meta(val asserts: MutableList<MetaAssert>) {
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
     * @param to The node target the [MetaAssert] should be applied to.
     *
     * @throws IllegalArgumentException if [assert]’s target doesn’t match [to].
     */
    fun add(assert: MetaAssert, to: Target) {
        require(assert.appliesTo(to)) {
            "The given assert does not apply to the given target."
        }

        asserts.add(assert)
    }
}