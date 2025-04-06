package check.sem

import ast.hierarchy.decl.Decl
import ast.hierarchy.program.Program
import visit.Visit

object SemResolver : Visit<Program, Program> {
    override fun visit(what: Program): Program {
        TODO()
    }
        /*
        return Program(what.decls.map { visitDecl(it) })
    }

    fun visitDecl(decl: Decl) = when (decl) {
        is Decl.Const -> visitConst(decl)
        is Decl.Consts -> visitConsts(decl)
        is Decl.StmtDecl -> visitStmt(decl.stmt)
    }

    fun visitConst(const: Decl.Const): Decl.Const {
        val expr = visitExpr(const.expr)
    }
         */
}