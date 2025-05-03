package meta.asserts

/**
 * Represents an **inline assertion**, i.e.:
 *
 * ```
 * "Hello" @[type = Str]
 * ```
 *
 * @param T The type of the value that needs to be compared against.
 * @param name The name of the assertion.
 * @param value The value being compared against—the expected value.
 */
sealed class InlineAssert<T>(val name: String, val value: T) {
    data class TypeAssert(val typeName: String) : InlineAssert<String>("type", typeName)
}