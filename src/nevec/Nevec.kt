package nevec

import check.Check
import cli.CliArgs
import cli.CliOptions
import ctx.Ctx
import err.report.Report
import file.contents.Src
import ir.lower.Lower
import ir.rendition.Rendition
import nevec.result.Aftermath
import nevec.result.Fail
import parse.Parse
import java.io.IOException

/**
 * Coordinates the process of compiling a Neve program.
 */
object Nevec {
    /**
     * Initiates the compilation phase.
     *
     * @return An [Aftermath] data object or class indicating whether compilation was successful.
     */
    fun run(args: Array<String>): Aftermath {
        val (file, cliOptions) = try {
            CliArgs.parse(args)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return Fail.CLI.wrap()
        }

        return runWithOptions(file, cliOptions)
    }

    /**
     * Compiles the given Neve program with the given [CliOptions].
     *
     * @return An [Aftermath] data object or class indicating whether compilation was successful.
     */
    fun runWithOptions(file: String, options: CliOptions): Aftermath {
        val ctx = Ctx(options)

        val (src, _) = try {
            Src.read(file)
        } catch (e: IOException) {
            Report.cliFileErr(file)
            return Fail.IO.wrap()
        }

        val parse = Parse(src, ctx)
        val parsed = parse.parse()

        if (parse.hadErr()) {
            return Fail.COMPILE.wrap()
        }

        val (resolved, success) = Check.check(parsed, ctx)
        if (!success) {
            return Fail.COMPILE.wrap()
        }

        val ir = Lower().visit(resolved)
        val rendition = Rendition(ir).new()
        println(rendition)

        return Aftermath.Success
    }
}