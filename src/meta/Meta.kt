package meta

import ast.info.impl.Infoful
import meta.comp.MetaComp
import meta.fail.MetaFail
import meta.result.MetaResult
import meta.target.Target

/**
 * Encapsulates both [meta assertions][meta.comp.asserts.MetaAssert] and `meta annotations` into a same data class,
 * using [MetaComp].
 *
 * @see MetaComp
 */
data class Meta(val comps: List<MetaComp>) {
    companion object {
        /**
         * @return A [Meta] data class with an empty list of meta components.
         */
        fun empty(): Meta {
            return Meta(listOf())
        }
    }

    /**
     * Tries to add a [meta component][MetaComp] to the current [Meta], producing a [MetaResult].
     *
     * @param comp The assert in question.
     * @param to The node target the [MetaComp] should be applied to.
     * @param of The node itself.
     *
     * @return a [MetaResult] with a [MetaFail] if [to] can’t be applied to [comp], a [MetaResult] containing
     * the new [Meta] otherwise.
     */
    fun add(comp: MetaComp, to: Target, of: Infoful) : MetaResult {
        return if (!comp.appliesTo(to))
            MetaResult.Fail(
                MetaFail.Target(comp, of)
            )
        else
            MetaResult.Success(
                Meta(comps + listOf(comp))
            )
    }
}