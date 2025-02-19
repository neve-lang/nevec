package cli

class CliOptions(private val options: List<Options>) {
    companion object {
        fun read(args: List<String>) = from(args.filter { it.startsWith("-") })
        private fun from(flags: List<String>) = CliOptions(flags.mapNotNull(Options::from))
    }

    fun isEnabled(option: Options) = options.contains(option)
}