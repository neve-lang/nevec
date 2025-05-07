package meta.comp

import meta.comp.asserts.MetaAssert
import meta.target.AppliesTo
import meta.target.Target

/**
 * Union-like sealed class that can either be a [meta assertion][MetaAssert] or a [meta annotation][MetaAnnot].
 */
sealed class MetaComp : AppliesTo {
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

    override fun appliesTo(target: Target) = when (this) {
        is OfAssert -> assert.appliesTo(target)
    }
}