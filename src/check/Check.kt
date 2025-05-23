package check

import ast.hierarchy.program.Program
import check.meta.MetaAssertCheck
import check.sem.SemResolver
import check.type.TypeCheck
import cli.Options
import ctx.Ctx

/**
 * Helper object to simplify the process of checking the source AST.
 */
object Check {
    /**
     * Applies all compile-time check phases to the given [Program].
     *
     * @return whether [what] is a valid Neve program or not.
     */
    fun check(what: Program, ctx: Ctx): Pair<Program, Boolean> {
        val resolved = SemResolver().visit(what)
        val typesOkay = TypeCheck().visit(resolved)

        if (!typesOkay) {
            return what to false
        }

        return if (ctx.options.isEnabled(Options.META_ASSERTS))
            resolved to MetaAssertCheck().visit(resolved)
        else
            resolved to true
    }
}