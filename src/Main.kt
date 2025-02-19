import cli.CliOptions
import cli.Options

fun main(args: Array<String>) {
    val cliOptions = CliOptions.read(args.toList())

    println("Running!!")

    if (cliOptions.isEnabled(Options.NO_OPT)) {
        println("Optimizations disabled.")
    }
}