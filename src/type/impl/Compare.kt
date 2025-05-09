package type.impl

import type.Type

/**
 * Provides two methods, [hasSameName] and [isIdentical] for [Types][Type].
 *
 * The reason why we provide two comparison methods, is that there are **two kinds of type comparisons in Neve**:
 *
 * - Comparing whether the name is the same, sometimes referred to as **shallow comparison**.  This is because Neve
 *   mainly uses nominal typing.
 * - Comparing whether the **domain** is the same.  If we define two types, `A` and `B`, both as `Int where self == 10`,
 *   then both types should be considered to have the same domain, but not the same identity.
 *
 * @see Type
 * @see hasSameName
 * @see isIdentical
 */
interface Compare {
    /**
     * @return Whether the implementor [Type] and [other] have the same name.
     *
     * This comparison method—shallow comparison—is often used within [meta assertions][meta.comp.asserts.MetaAssert].
     */
    fun hasSameName(other: Type): Boolean

    /**
     * @return Whether the implementor [Type] and [to] have the same [Domain][domain.Domain].
     *
     * This comparison method is the most used one.
     *
     * @see domain.Domain
     */
    fun isIdentical(other: Type): Boolean
}