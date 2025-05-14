package cli

/**
 * A barebones CLI argument parser.
 */
object CliArgs {
    /**
     * Takes the array of arguments and parses it.
     *
     * @param args The array of arguments given by the main function.
     * @return A pair of String and [CliOptions], where the String is the file name.
     *
     * @throws IllegalArgumentException if no arguments are given.
     */
    fun parse(args: Array<String>): Pair<String, CliOptions> {
        require(args.isNotEmpty()) {
            "usage: nevec <file> [options]"
        }

        val file = args[0]
        return Pair(file, CliOptions.read(args))
    }
}