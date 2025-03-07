import cli.CliArgs
import err.report.Report
import file.contents.Src
import java.io.IOException

fun main(args: Array<String>) {
    val (file, cliOptions) = try {
        CliArgs.parse(args)
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return
    }

    val (src, lines) = try {
        Src.read(file)
    } catch (e: IOException) {
        Report.fileErr(file)
        return
    }
}