package type.impl

import type.Type

/**
 * Provides a method [isSame] comparison method for [Types][Type].
 *
 * @see Type
 * @see isSame
 * @see isIdentical
 */
interface Compare<To: Compare<To>> {
    /**
     * @return Whether the implementor type and [other] have the same name.
     *
     * For more complex types, such as [Applied][type.gen.Applied], the type parameters are compared too.
     *
     * This comparison method—shallow comparison—is often used within [meta assertions][meta.comp.asserts.MetaAssert].
     */
    fun isSame(other: To): Boolean
}