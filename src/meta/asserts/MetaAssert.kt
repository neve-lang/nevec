package meta.asserts

import meta.target.Target
import type.Type

/**
 * Represents a **meta assertion**, i.e.:
 *
 * ```
 * "Hello" @[type = Str]
 * ```
 *
 * One peculiarity of meta assertions is that they **always appear after their target**, whereas meta annotations
 * appear before it.
 *
 * @param T The type of the value that needs to be compared against.
 * @param value The value being compared against—the expected value.
 * @param target The type of
 */
sealed class MetaAssert<T>(val value: T, val target: Target) {
    /**
     * A **type** meta assertion.
     *
     * Used to check the [Type][type.Type] of a **primary** expression.
     *
     * @see type.Type
     */
    data class TypeAssert(val type: Type) : MetaAssert<Type>(
        type,
        Target.PRIMARY
    )
}