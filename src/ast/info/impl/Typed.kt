package ast.info.impl

import type.Type

/**
 * Interface that requires an AST node to carry a type.
 *
 * @see Type
 */
interface Typed {
    /**
     * @return The implementor node’s type.
     */
    fun type(): Type
}