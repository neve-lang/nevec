package meta.comp

import meta.comp.asserts.MetaAssert

/**
 * Union-like sealed class that can either be a [meta assertion][MetaAssert] or a [meta annotation][MetaAnnot].
 */
sealed class MetaComp {
    companion object {
        /**
         * Wraps a [MetaAssert] into a [MetaComp].
         */
        fun from(assert: MetaAssert): MetaComp {
            return OfAssert(assert)
        }
    }

    /**
     * Wrapper around a [MetaAssert].
     *
     * @property assert The meta assertion itself.
     *
     * @see MetaAssert
     */
    data class OfAssert(val assert: MetaAssert) : MetaComp()
}