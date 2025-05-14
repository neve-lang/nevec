package meta

import meta.comp.MetaComp
import meta.comp.asserts.MetaAssert
import meta.result.MetaResult

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

        /**
         * @return A [Meta] data class with the list of [comps] given.
         */
        fun from(vararg comps: MetaComp): Meta {
            return Meta(comps.toList())
        }
    }

    /**
     * @return A new [Meta] with the lists of both operands concatenated.
     */
    operator fun plus(other: Meta): Meta {
        return Meta(comps + other.comps)
    }

    /**
     * @return The list of [meta assertions][MetaAssert] contained in [comps].
     */
    fun asserts(): List<MetaAssert> {
        return comps.filterIsInstance<MetaAssert>()
    }

    /**
     * @return Whether [comps] is empty.
     */
    fun isEmpty(): Boolean {
        return comps.isEmpty()
    }

    /**
     * @return A [MetaResult] wrapped around `this`.
     *
     * @see MetaResult
     */
    fun wrap(): MetaResult {
        return MetaResult.Success(this)
    }
}