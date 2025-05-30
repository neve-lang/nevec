package check.meta

import ast.hierarchy.binop.BinOp
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.top.Top
import ast.hierarchy.unop.UnOp
import ast.info.Info
import ctx.Ctx
import err.msg.Msg
import meta.comp.asserts.MetaAssert
import nevec.result.Aftermath
import nevec.result.Fail
import stage.Stage

/**
 * Small visitor pattern that traverses a [Program] and checks [meta assertions][MetaAssert], producing a compiler
 * error if for each fail.
 *
 * Its [perform] method returns `true` if no meta assertions failed, or it returns `false` otherwise.
 */
class MetaAssertCheck : Stage<Program, Program> {
    override fun perform(data: Program, ctx: Ctx): Aftermath<Program> {
        // not using
        // `what.decls.all(::visitTop)`
        // to avoid short-circuiting.
        return data.decls.map(::visitTop).all { it }.let { okay ->
            if (okay)
                Aftermath.Success(data)
            else
                Fail.CHECK.wrap()
        }
    }

    private fun visitTop(top: Top) = when (top) {
        is Top.Fun -> visitFun(top)
        else -> okay()
    }

    private fun visitFun(node: Top.Fun): Boolean {
        return node.decls.map(::visitDecl).all { it }
    }

    private fun visitDecl(decl: Decl) = when (decl) {
        is Decl.OfStmt -> visitStmt(decl.stmt)
    }

    private fun visitStmt(stmt: Stmt) = when (stmt) {
        is Stmt.Print -> okay()
        is Stmt.OfExpr -> visitExpr(stmt.expr)
    }

    private fun visitExpr(expr: Expr) = when (expr) {
        is Expr.Show -> visitShow(expr)
        is Expr.Parens -> visitParens(expr)

        is Expr.OfUnOp -> visitUnOp(expr.unOp)
        is Expr.OfBinOp -> visitBinOp(expr.binOp)
        is Expr.OfLit -> visitLit(expr.lit)
        is Expr.OfInterpol -> visitInterpol(expr.interpol)

        is Expr.Empty -> visitEmpty(expr)
    }

    private fun visitShow(show: Expr.Show): Boolean {
        return check(show.info())
    }

    private fun visitParens(parens: Expr.Parens): Boolean {
        return check(parens.info()) and
                visitExpr(parens.expr)
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitNeg(unOp)
        is UnOp.Not -> visitNot(unOp)
    }

    private fun visitNeg(neg: UnOp.Neg): Boolean {
        return check(neg.info()) && visitExpr(neg.expr)
    }

    private fun visitNot(not: UnOp.Not): Boolean {
        return check(not.info()) && visitExpr(not.expr)
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> visitBitwise(binOp)
        is BinOp.Arith -> visitArith(binOp)
        is BinOp.Comp -> visitComp(binOp)
        is BinOp.Concat -> visitConcat(binOp)
    }

    private fun visitBitwise(bitwise: BinOp.Bitwise): Boolean {
        return check(bitwise.info()) and
                visitExpr(bitwise.left) and
                visitExpr(bitwise.right)
    }

    private fun visitArith(arith: BinOp.Arith): Boolean {
        return check(arith.info()) and
                visitExpr(arith.left) and
                visitExpr(arith.right)
    }

    private fun visitComp(comp: BinOp.Comp): Boolean {
        return check(comp.info()) and
                visitExpr(comp.left) and
                visitExpr(comp.right)
    }

    private fun visitConcat(concat: BinOp.Concat): Boolean {
        return check(concat.info()) and
                visitExpr(concat.left) and
                visitExpr(concat.right)
    }

    private fun visitLit(lit: Lit) = when (lit) {
        is Lit.IntLit -> visitInt(lit)
        is Lit.FloatLit -> visitFloat(lit)
        is Lit.BoolLit -> visitBool(lit)
        is Lit.StrLit -> visitStr(lit)
        is Lit.TableLit -> visitTable(lit)
        is Lit.NilLit -> visitNil(lit)
    }

    private fun visitInt(int: Lit.IntLit): Boolean {
        return check(int.info())
    }

    private fun visitFloat(float: Lit.FloatLit): Boolean {
        return check(float.info())
    }

    private fun visitBool(bool: Lit.BoolLit): Boolean {
        return check(bool.info())
    }

    private fun visitStr(str: Lit.StrLit): Boolean {
        return check(str.info())
    }

    private fun visitTable(table: Lit.TableLit): Boolean {
        // not using
        // `table.keys.all { check(it.info()) }`
        // to avoid short-circuiting.
        return check(table.info()) and
                table.keys.map { check(it.info()) }.all { it } and
                table.vals.map { check(it.info()) }.all { it }
    }

    private fun visitNil(nil: Lit.NilLit): Boolean {
        return check(nil.info())
    }

    private fun visitInterpol(interpol: Interpol) = when (interpol) {
        is Interpol.Some -> visitSome(interpol)
        is Interpol.End -> visitEnd(interpol)
    }

    private fun visitSome(some: Interpol.Some): Boolean {
        return check(some.info()) and
                visitExpr(some.expr) and
                visitInterpol(some.next)
    }

    private fun visitEnd(end: Interpol.End): Boolean {
        return check(end.info())
    }

    private fun visitEmpty(empty: Expr.Empty): Boolean {
        return check(empty.info())
    }

    private fun check(info: Info): Boolean {
        if (info.meta().isEmpty()) {
            return okay()
        }

        val msgs = info.meta().asserts().mapNotNull {
            it.pickMsg(info)?.let(Msg::print)
        }

        return msgs.isEmpty()
    }

    private fun okay(): Boolean {
        return true
    }
}