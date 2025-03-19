package ast.pretty

import ast.hierarchy.binop.BinOp
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.unop.UnOp
import type.pretty.PrettyType
import util.extension.*
import visit.Visit

/**
 * A pretty-printer for the AST.
 */
object Pretty : Visit<Program, String> {
    override fun visit(what: Program): String {
        return what.decls.joinToString("\n") { visitDecl(it) }
    }

    fun visitDecl(decl: Decl) = when (decl) {
        is Decl.Const -> visitConst(decl)
        is Decl.Consts -> visitConsts(decl).joinToString("\n")
        is Decl.StmtDecl -> visitStmt(decl.stmt)
    }

    fun visitStmt(stmt: Stmt) = when (stmt) {
        is Stmt.Print -> visitPrint(stmt)
        is Stmt.ExprStmt -> visitExpr(stmt.expr)
    }

    fun visitExpr(expr: Expr): String = when (expr) {
        is Expr.UnOpExpr -> visitUnOp(expr.unOp)
        is Expr.BinOpExpr -> visitBinOp(expr.binOp)
        is Expr.LitExpr -> visitLit(expr.lit)
        is Expr.InterpolExpr -> visitInterpol(expr.interpol)
        is Expr.Parens -> visitParens(expr)
        is Expr.Access -> visitAccess(expr)
        is Expr.AccessConst -> visitAccessConst(expr)
        is Expr.Empty -> visitEmpty(expr)
        is Expr.Show -> visitShow(expr)
    }

    private fun visitConst(const: Decl.Const): String {
        val name = const.name
        val type = PrettyType.pretty(const.type)
        val expr = visitExpr(const.expr)

        return "$name $type = $expr"
    }

    private fun visitConsts(consts: Decl.Consts): List<String> {
        return consts.consts.map { visitConst(it) }.indent().wrappedIn(begin = "const ${consts.name}", end = "end")
    }

    private fun visitPrint(print: Stmt.Print): String {
        return "print ${visitExpr(print.expr)}"
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitExpr(unOp.expr).prefixWith("-")
        is UnOp.Not -> visitExpr(unOp.expr).prefixWith("not ")
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> binOp.operands().map { visitExpr(it) }.infixWith(PrettyOperator.pretty(binOp.operator))
        is BinOp.Arith -> binOp.operands().map { visitExpr(it) }.infixWith(PrettyOperator.pretty(binOp.operator))
        is BinOp.Comp -> binOp.operands().map { visitExpr(it) }.infixWith(PrettyOperator.pretty(binOp.operator))
        is BinOp.Concat -> binOp.operands().map { visitExpr(it) }.infixWith(" + ")
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

    private fun visitAccess(access: Expr.Access): String {
        return access.tok.lexeme
    }

    private fun visitAccessConst(access: Expr.AccessConst): String {
        return access.name
    }

    private fun visitEmpty(empty: Expr.Empty): String {
        return "(empty)"
    }

    private fun visitShow(show: Expr.Show): String {
        return "${visitExpr(show.expr)}.show"
    }
}