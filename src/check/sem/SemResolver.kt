package check.sem

import ast.hierarchy.program.Program
import visit.Visit

object SemResolver : Visit<Program, Program> {
    override fun visit(what: Program): Program {
        TODO()
    }
}