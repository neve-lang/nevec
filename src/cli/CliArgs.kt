package cli

object CliArgs {
    fun parse(args: Array<String>): Pair<String, CliOptions> {
        require(args.isNotEmpty()) {
            "usage: nevec <file> [--no-opt]"
        }

        val file = args[0]
        return Pair(file, CliOptions.read(args))
    }
}