package check

import ast.hierarchy.program.Program
import check.meta.MetaAssertCheck
import check.sem.SemResolver
import check.type.TypeCheck
import cli.Options
import ctx.Ctx
import nevec.result.Aftermath
import stage.Stage
import stage.travel.AliveTravel

/**
 * The checking stage in the compilation process.
 */
class Check : Stage<Program, Program> {
    /**
     * Applies all compile-time check stages to the given [Program].
     *
     * @return whether [data] is a valid Neve program or not.
     */
    override fun perform(data: Program, ctx: Ctx): Aftermath<Program> {
        return AliveTravel(data, ctx)
            .proceedWith(::SemResolver)
            .proceedWith(::TypeCheck)
            .ifEnabled(Options.META_ASSERTS, ::MetaAssertCheck)
            .finish()
    }
}