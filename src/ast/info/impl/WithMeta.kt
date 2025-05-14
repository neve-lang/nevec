package ast.info.impl

import meta.Meta

/**
 * Provides a [meta] method that allows accessing the node’s [Meta].
 */
interface WithMeta {
    /**
     * @return The implementor node’s meta information.
     */
    fun meta(): Meta
}