package lower

import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.top.Top
import csr.hierarchy.expr.CsrExpr
import csr.hierarchy.program.CsrProgram
import csr.hierarchy.top.CsrTop
import ctx.Ctx
import nevec.result.Aftermath
import stage.Stage

/**
 * Lowers the Neve AST down to the CSR (Caml-suited representation.)
 */
class Lower : Stage<Program, CsrProgram> {
    override fun perform(data: Program, ctx: Ctx): Aftermath<CsrProgram> {
        return data.decls
            .map(::visitTop)
            .let(::CsrProgram)
            .let {
                Aftermath.Success(it)
            }
    }

    private fun visitTop(top: Top) = when (top) {
        is Top.Fun -> visitFun(top)
    }

    private fun visitFun(topFun: Top.Fun): CsrTop.Fun {
        if (topFun.decls.size > 1) {
            throw UnsupportedOperationException(
                "Only single-expression functions are supported at the moment."
            )
        }

        return visitDecl(topFun.decls.first()).let {
            if (isMainFun(topFun))
                CsrTop.MainFun(it)
            else
                CsrTop.Fun(it)
        }
    }

    private fun visitDecl(decl: Decl) = when (decl) {
        is Decl.OfStmt -> visitStmt(decl)
    }

    private fun visitStmt(stmt: Stmt) = when (stmt) {
        is Stmt.Print -> visitPrint(stmt)
        is Stmt.OfExpr -> visitExpr(stmt.expr)
    }

    private fun visitPrint(print: Stmt.Print): CsrExpr.Print {
        return CsrExpr.Print(visitExpr(print.expr))
    }

    private fun visitExpr(expr: Expr) = when (expr) {
        is Expr.Parens -> visitParens(expr)
        is Expr.OfUnOp -> visitUnOp(expr)
        is Expr.OfLit -> visitLit(expr)

        is Expr.Show, Expr.Empty, Expr.OfInterpol -> visitShow(expr)
    }

    /**
     * Simply checks whether this function’s name is `main`.
     *
     * Once we introduce a module system, more sophisticated checking will be done.
     */
    private fun isMainFun(topFun: Top.Fun): Boolean {
        return topFun.name == "main"
    }
}