package pretty

import ast.hierarchy.binop.BinOp
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.top.Top
import ast.hierarchy.unop.UnOp
import util.extension.*

/**
 * A pretty-printer for the AST.
 */
object Pretty {
    fun visit(program: Program): String {
        return program.decls.joinToString("\n") { visitTop(it) }
    }

    private fun visitTop(top: Top) = when (top) {
        is Top.Fun -> visitFun(top)
        is Top.Empty -> visitTopEmpty()
    }

    private fun visitFun(node: Top.Fun): String {
        return "fun ${node.name}\n" +
                indented(node.decls.map(::visitDecl)) +
               "end\n"
    }

    private fun visitTopEmpty(): String {
        return "(empty)"
    }

    private fun visitDecl(decl: Decl) = when (decl) {
        is Decl.OfStmt -> visitStmt(decl.stmt)
    }

    private fun visitStmt(stmt: Stmt) = when (stmt) {
        is Stmt.Print -> visitPrint(stmt)
        is Stmt.OfExpr -> visitExpr(stmt.expr)
    }

    private fun visitExpr(expr: Expr): String = when (expr) {
        is Expr.OfUnOp -> visitUnOp(expr.unOp)
        is Expr.OfBinOp -> visitBinOp(expr.binOp)
        is Expr.OfLit -> visitLit(expr.lit)
        is Expr.OfInterpol -> visitInterpol(expr.interpol)
        is Expr.Parens -> visitParens(expr)
        // is Expr.Access -> visitAccess(expr)
        // is Expr.AccessConst -> visitAccessConst(expr)
        is Expr.Empty -> visitEmpty()
        is Expr.Show -> visitShow(expr)
    }

    private fun visitPrint(print: Stmt.Print): String {
        return "print ${visitExpr(print.expr)}"
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitExpr(unOp.expr).prefixWith("-")
        is UnOp.Not -> visitExpr(unOp.expr).prefixWith("not ")
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> binOp.operands().map {
            visitExpr(it)
        }.infixWith(PrettyOperator.pretty(binOp.operator))

        is BinOp.Arith -> binOp.operands().map {
            visitExpr(it)
        }.infixWith(PrettyOperator.pretty(binOp.operator))

        is BinOp.Comp -> binOp.operands().map {
            visitExpr(it)
        }.infixWith(PrettyOperator.pretty(binOp.operator))

        is BinOp.Concat -> binOp.operands().map {
            visitExpr(it)
        }.infixWith(PrettyOperator.pretty(binOp.operator))
    }

    private fun visitLit(lit: Lit) = when (lit) {
        is Lit.IntLit -> lit.value.toString()
        is Lit.FloatLit -> lit.value.toString()
        is Lit.StrLit -> lit.value
        is Lit.BoolLit -> lit.value.toString()

        is Lit.NilLit -> "nil"
        is Lit.TableLit -> visitTableLit(lit)
    }

    private fun visitTableLit(table: Lit.TableLit): String {
        if (table.keys.isEmpty()) {
            return "[:]"
        }

        val keys = table.keys.map { visitExpr(it) }
        val vals = table.vals.map { visitExpr(it) }

        val pairs = keys.zip(vals)
        return pairs.map { it.infixWith(": ") }.wrappedIn("[", "]").joinToString(", ")
    }

    private fun visitInterpol(interpol: Interpol): String {
        return when (interpol) {
            is Interpol.End -> interpol.string
            is Interpol.Some -> {
                val string = interpol.string.trimQuotesAround()
                val expr = visitExpr(interpol.expr)
                val next = visitInterpol(interpol.next).trimQuotesAround()

                "$string#{$expr}$next".wrappedInQuotes()
            }
        }
    }

    private fun visitParens(parens: Expr.Parens): String {
        return visitExpr(parens.expr).parenthesized()
    }

    /*
    private fun visitAccess(access: Expr.Access): String {
        return access.name
    }

    private fun visitAccessConst(access: Expr.AccessConst): String {
        return access.name
    }
     */

    private fun visitEmpty(): String {
        return "(empty)"
    }

    private fun visitShow(show: Expr.Show): String {
        return "${visitExpr(show.expr)}.show"
    }

    private fun indented(strings: List<String>): String {
        return strings.indent().joinToString("\n")
    }
}