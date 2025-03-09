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

        fun from(string: String) = MAP[string]
    }
}