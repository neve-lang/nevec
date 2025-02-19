package cli

enum class Options {
    NO_OPT;

    companion object {
        private val MAP = mapOf(
            "--no-opt" to NO_OPT
        )

        fun from(string: String): Options? = MAP[string]
    }
}