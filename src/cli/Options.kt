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
    NO_OPT;

    companion object {
        private val MAP = mapOf(
            "--no-opt" to NO_OPT
        )

        /**
         * @return An [Options] variant from [string] if it’s a valid option, `null` otherwise.
         */
        fun from(string: String): Options? {
            return MAP[string]
        }
    }
}