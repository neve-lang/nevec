package cli

/**
 * Possible CLI Options.
 */
enum class Options {
    /**
     * Disables all non-memory related optimizations.
     *
     * Optimizations that *will remain* include:
     *  - Constant propagation
     */
    NO_OPT,

    /**
     * Enables the [inline assertions][meta.asserts.InlineAssert] feature.
     *
     * Allows the user to perform metaprogramming assertions, such as:
     *
     * ```
     * ("Hello, " + "10") @[type = Str]
     * ```
     *
     * The compiler will then check each inline assertion in the AST.
     *
     * Please note that inline assertion errors do not prevent the compiler from proceeding with compilation.
     *
     * @see meta.asserts.InlineAssert
     */
    INLINE_ASSERTS,

    /**
     * Prevents the compiler from going further than the type-checking phase, i.e. lowering the AST to the IR
     * representation.
     */
    CHECK_ONLY;

    companion object {
        private val MAP = mapOf(
            "--no-opt" to NO_OPT,
            "--inline-asserts" to INLINE_ASSERTS,
            "--check-only" to CHECK_ONLY
        )

        /**
         * @return An [Options] variant from [string] if it’s a valid option, `null` otherwise.
         */
        fun from(string: String): Options? {
            return MAP[string]
        }
    }
}