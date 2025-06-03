package nevec

import ast.hierarchy.program.Program
import check.Check
import cli.CliArgs
import cli.CliOptions
import cli.Options
import ctx.Ctx
import err.report.Report
import file.contents.Src
import ir.lower.Lower
import ir.rendition.Rendition
import nevec.result.Aftermath
import nevec.result.Fail
import stage.travel.AliveTravel
import parse.ParseStage
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
    fun run(args: Array<String>): Aftermath<Unit> {
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
    fun runWithOptions(file: String, options: CliOptions): Aftermath<Unit> {
        val ctx = Ctx(options)

        val (src, _) = try {
            Src.read(file)
        } catch (e: IOException) {
            Report.cliFileErr(file)
            return Fail.IO.wrap()
        }

        return AliveTravel(src, ctx)
            .proceedWith(::ParseStage)
            .proceedWith(::Check)
            .finish()
            .let { aftermath ->
                if (ctx.isEnabled(Options.CHECK_ONLY))
                    aftermath.into(Unit)
                else
                    lowerStage(aftermath, ctx)
            }
    }

    private fun lowerStage(aftermath: Aftermath<Program>, ctx: Ctx): Aftermath<Unit> {
        if (aftermath.isFail()) {
            return aftermath.into(Unit)
        }

        val program = aftermath.cure()!!.result

        return AliveTravel(program, ctx)
            .proceedWith(::Lower)
            .proceedWith(::Rendition)
            .finish()
            .into(Unit)
    }
}