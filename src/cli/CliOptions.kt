package cli

/**
 * The list of CLI [Options].
 *
 * @property options The list of *enabled* options.  Disabled options will not be stored.
 */
data class CliOptions(private val options: List<Options>) {
    companion object {
        /**
         * Takes the array of arguments and builds a [CliOptions].
         *
         * @param args The CLI args in question.
         *
         * @return A [CliOptions] containing all enabled [Options].
         */
        fun read(args: Array<String>): CliOptions {
            val flags = args.filter { it.startsWith("-") }
            return from(flags)
        }

        private fun from(flags: List<String>): CliOptions {
            return CliOptions(flags.mapNotNull(Options::from))
        }
    }

    /**
     * @return whether [option] is enabled.
     */
    fun isEnabled(option: Options): Boolean {
        return option in options
    }
}