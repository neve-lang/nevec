package type.impl

import type.Type

/**
 * Provides a methods [hasSameName] comparison method for [Types][Type].
 *
 * @see Type
 * @see hasSameName
 * @see isIdentical
 */
interface Compare<To: Compare<To>> {
    /**
     * @return Whether the implementor type and [other] have the same name.
     *
     * This comparison method—shallow comparison—is often used within [meta assertions][meta.comp.asserts.MetaAssert].
     */
    fun hasSameName(other: To): Boolean
}