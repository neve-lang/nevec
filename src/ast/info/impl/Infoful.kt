package ast.info.impl

import ast.info.Info

/**
 * Interface that includes all [Info]-related interfaces into one.
 *
 * It also provides a couple new methods:
 *
 * - An [update] method, which produces an identical node to that of the implementor node, except
 * with a new [Info] data class.
 *
 * - An [info] method, which returns the implementor’s [Info] data class attached to it.
 *
 * @see Info
 */
interface Infoful : Spanned, Typed, WithMeta {
    /**
     * Produces an identical node to that of the implementor node, but with a new [Info][ast.info.Info] data class.
     */
    fun update(new: Info): Infoful

    /**
     * @return The implementor node’s [Info].
     */
    fun info(): Info
}