package ast.info.impl

import file.span.Loc

/**
 * Interface that requires an AST to carry a [Loc].
 *
 * @see Loc
 */
interface Spanned {
    /**
     * @return the implementor node’s [Loc].
     */
    fun loc(): Loc
}