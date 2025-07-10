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
     *  - Dead term elimination
     *  - Constant reuse
     */
    NO_OPT,

    /**
     * Enables the [meta assertions][meta.comp.asserts.MetaAssert] feature.
     *
     * Allows the user to perform metaprogramming assertions, such as:
     *
     * ```
     * ("Hello, " + "10") @[type == Str]
     * ```
     *
     * The compiler will then check each meta assertion in the AST.
     *
     * Please note that meta assertion errors do not prevent the compiler from proceeding with compilation.
     *
     * @see meta.comp.asserts.MetaAssert
     */
    META_ASSERTS,

    /**
     * Prevents the compiler from going further than the type-checking phase, i.e. lowering the AST to the IR
     * representation.
     */
    CHECK_ONLY,

    /**
     * Allows referencing types that are usually not exposed to the user, such as [poisoned types][type.poison.Poison],
     * [free types][type.gen.Free] and more.
     *
     * Each of these “compiler types” is given special syntax, such as:
     *
     * ```
     * ~Unknown -- poisoned type
     * '0 -- free type
     * ```
     */
    COMPILER_TYPES,

    /**
     * Displays the **unoptimized** IR dump to standard output after lowering the AST to IR.
     */
    SHOW_IR,

    /**
     * Displays the **optimized** IR dump to standard output after the optimization stage.
     */
    SHOW_OPT_IR,

    /**
     * Disables the constant folding IR optimization.
     */
    OPT_NO_CONST_FOLD,

    /**
     * Disables the table propagation IR optimization.
     */
    OPT_NO_TABLE_PROPAGATION,

    /**
     * Disables the constant reuse IR optimization.
     */
    OPT_NO_CONST_REUSE;

    companion object {
        private val MAP = mapOf(
            "--no-opt" to NO_OPT,
            "--meta-asserts" to META_ASSERTS,
            "--check-only" to CHECK_ONLY,
            "--compiler-types" to COMPILER_TYPES,
            "--show-ir" to SHOW_IR,
            "--show-opt-ir" to SHOW_OPT_IR,
            "--opt-no-const-fold" to OPT_NO_CONST_FOLD,
            "--opt-no-table-propagation" to OPT_NO_TABLE_PROPAGATION,
            "--opt-no-const-reuse" to OPT_NO_CONST_REUSE
        )

        /**
         * @return An [Options] variant from [string] if it’s a valid option, `null` otherwise.
         */
        fun from(string: String): Options? {
            return MAP[string]
        }
    }
}