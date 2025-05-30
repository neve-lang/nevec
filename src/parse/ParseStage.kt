package parse

import ast.hierarchy.program.Program
import ctx.Ctx
import nevec.result.Aftermath
import nevec.result.Fail
import stage.Stage

/**
 * Wrapper around the [Parse] class that is [Stage]-compliant.
 */
class ParseStage : Stage<String, Program> {
    override fun perform(data: String, ctx: Ctx): Aftermath<Program> {
        return Parse(data, ctx).let {
            val parsed = it.parse()

            if (!it.hadErr())
                Aftermath.Success(parsed)
            else
                Aftermath.OfFail(Fail.PARSE)
        }
    }
}