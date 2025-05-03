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
 * @param T The type of the value that needs to be compared against.
 * @param name The name of the assertion.
 * @param value The value being compared against—the expected value.
 * @param target The type of
 */
sealed class MetaAssert<T>(val name: String, val value: T, val target: Target) {
    /**
     * A **type** meta assertion.
     *
     * Used to check the [Type][type.Type] of a **primary** expression.
     *
     * @see type.Type
     */
    data class TypeAssert(val type: Type) : MetaAssert<Type>(
        "type",
        type,
        Target.PRIMARY
    )
}