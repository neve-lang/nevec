import cli.CliArgs
import cli.Options

fun main(args: Array<String>) {
    val (file, cliOptions) = try {
        CliArgs.parse(args)
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return
    }

    println("Running with $file!!")

    if (cliOptions.isEnabled(Options.NO_OPT)) {
        println("Optimizations disabled.")
    }
}