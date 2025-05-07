package ast.info.impl

import ast.info.Info

/**
 * Interface that includes all [Info]-related interfaces into one.
 *
 * It also provides an [update] method, which produces an identical node to that of the implementor node, except
 * with a new [Info] data class.
 *
 * @see Info
 */
interface Infoful : Spanned, Typed, WithMeta {
    /**
     * Produces an identical node to that of the implementor node, but with a new [Info][ast.info.Info] data class.
     */
    fun update(new: Info): Infoful
}