package cli

/**
 * The list of CLI [Options].
 *
 * @property options The list of *enabled* options.  Disabled options will not be stored.
 */
class CliOptions(private val options: List<Options>) {
    companion object {
        fun read(args: Array<String>): CliOptions {
            val flags = args.filter { it.startsWith("-") }
            return from(flags)
        }

        private fun from(flags: List<String>): CliOptions {
            return CliOptions(flags.mapNotNull(Options::from))
        }
    }

    fun isEnabled(option: Options) = options.contains(option)
}