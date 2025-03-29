package check

import ast.hierarchy.program.Program

/**
 * Helper object to simplify the process of checking the source AST.
 */
object Check {
    /**
     * Applies all compile-time check phases to the given [Program].
     *
     * @return whether [what] is a valid Neve program or not.
     */
    fun check(what: Program): Boolean {
        // TODO: not yet implemented
        return true
    }
}