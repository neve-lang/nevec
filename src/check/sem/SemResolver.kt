package check.sem

import ast.hierarchy.binop.BinOp
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.unop.UnOp
import infer.info.FromInfo
import infer.Infer
import util.extension.map
import visit.Visit

/**
 * The semantic resolving phase.
 *
 * This phase takes in a [Program] node, performs type inference using [Infer] and solves syntax ambiguities.
 *
 * It then produces a new [Program] node with types inferred and ambiguities solved.
 */
class SemResolver : Visit<Program, Program> {
    private val infer: Infer = Infer()

    override fun visit(what: Program): Program {
        return Program(what.decls.map { visitDecl(it) })
    }

    private fun visitDecl(decl: Decl) = when (decl) {
        is Decl.OfStmt -> visitStmt(decl.stmt).wrap()
    }

    private fun visitStmt(stmt: Stmt) = when (stmt) {
        is Stmt.Print -> visitPrint(stmt)
        is Stmt.OfExpr -> visitExpr(stmt.expr).wrap()
    }

    private fun visitPrint(print: Stmt.Print): Stmt.Print {
        return Stmt.Print(visitExpr(print.expr), print.loc)
    }

    private fun visitExpr(expr: Expr) = when (expr) {
        is Expr.Show -> visitShow(expr)
        is Expr.Parens -> visitParens(expr)

        is Expr.OfUnOp -> visitUnOp(expr.unOp).wrap()
        is Expr.OfBinOp -> visitBinOp(expr.binOp).wrap()
        is Expr.OfLit -> visitLit(expr.lit).wrap()
        is Expr.OfInterpol -> visitInterpol(expr.interpol).wrap()

        is Expr.Empty -> expr
    }

    private fun visitShow(show: Expr.Show): Expr.Show {
        return Expr.Show(visitExpr(show.expr), show.info)
    }

    private fun visitParens(parens: Expr.Parens): Expr.Parens {
        val expr = visitExpr(parens.expr)
        val new = Expr.Parens(expr, parens.info)

        return Expr.Parens(
            new.expr,
            new.info().withType(of = expr.type())
        )
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitNeg(unOp)
        is UnOp.Not -> visitNot(unOp)
    }

    private fun visitNeg(neg: UnOp.Neg): UnOp.Neg {
        val expr = visitExpr(neg.expr)
        val new = UnOp.Neg(expr, neg.info)

        return UnOp.Neg(
            new.expr,
            FromInfo(new.info).infer(from = new.wrap(), with = infer)
        )
    }

    private fun visitNot(not: UnOp.Not): UnOp.Not {
        val expr = visitExpr(not.expr)
        val new = UnOp.Not(expr, not.info)

        return UnOp.Not(
            new.expr,
            FromInfo(new.info).infer(from = new.wrap(), with = infer)
        )
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> visitBitwise(binOp)
        is BinOp.Arith -> visitArith(binOp)
        is BinOp.Comp -> visitComp(binOp)

        is BinOp.Concat -> throw IllegalArgumentException(
            "A concat node may not appear during the semantic resolving phase."
        )
    }

    private fun visitBitwise(bitwise: BinOp.Bitwise): BinOp.Bitwise {
        val (left, right) = bitwise.operands().map(::visitExpr)
        val new = BinOp.Bitwise(left, bitwise.operator, right, bitwise.info)

        return BinOp.Bitwise(
            new.left,
            new.operator,
            new.right,
            FromInfo(new.info).infer(from = new.wrap(), with = infer)
        )
    }

    private fun visitArith(arith: BinOp.Arith): BinOp.Arith {
        // TODO: once we implement type inference, implement Arith to Concat conversion.
        val (left, right) = arith.operands().map(::visitExpr)
        val new = BinOp.Arith(left, arith.operator, right, arith.info)

        return BinOp.Arith(
            new.left,
            new.operator,
            new.right,
            FromInfo(new.info).infer(from = new.wrap(), with = infer)
        )
    }

    private fun visitComp(comp: BinOp.Comp): BinOp.Comp {
        val (left, right) = comp.operands().map(::visitExpr)
        val new = BinOp.Comp(left, comp.operator, right, comp.info)

        return BinOp.Comp(
            new.left,
            new.operator,
            new.right,
            FromInfo(new.info).infer(from = new.wrap(), with = infer)
        )
    }

    private fun visitLit(lit: Lit) = when (lit) {
        is Lit.IntLit -> visitInt(lit)
        is Lit.FloatLit -> visitFloat(lit)
        is Lit.BoolLit -> visitBool(lit)
        is Lit.StrLit -> visitStr(lit)
        is Lit.TableLit -> visitTable(lit)
        is Lit.NilLit -> visitNil(lit)
    }

    private fun visitInt(int: Lit.IntLit): Lit.IntLit {
        return Lit.IntLit(
            int.value,
            FromInfo(int.info).infer(from = int.wrap(), with = infer)
        )
    }

    private fun visitFloat(float: Lit.FloatLit): Lit.FloatLit {
        return Lit.FloatLit(
            float.value,
            FromInfo(float.info).infer(from = float.wrap(), with = infer)
        )
    }

    private fun visitBool(bool: Lit.BoolLit): Lit.BoolLit {
        return Lit.BoolLit(
            bool.value,
            FromInfo(bool.info).infer(from = bool.wrap(), with = infer)
        )
    }

    private fun visitStr(str: Lit.StrLit): Lit.StrLit {
        return Lit.StrLit(
            str.value,
            FromInfo(str.info).infer(from = str.wrap(), with = infer)
        )
    }

    private fun visitTable(table: Lit.TableLit): Lit.TableLit {
        val keys = table.keys.map(::visitExpr)
        val vals = table.vals.map(::visitExpr)
        val new = Lit.TableLit(keys, vals, table.info)

        return Lit.TableLit(
            new.keys,
            new.vals,
            FromInfo(new.info).infer(from = new.wrap(), with = infer)
        )
    }

    private fun visitNil(nil: Lit.NilLit): Lit.NilLit {
        return Lit.NilLit(
            FromInfo(nil.info).infer(from = nil.wrap(), with = infer)
        )
    }

    private fun visitInterpol(interpol: Interpol) = when (interpol) {
        is Interpol.Some -> visitSome(interpol)
        is Interpol.End -> visitEnd(interpol)
    }

    private fun visitSome(some: Interpol.Some): Interpol.Some {
        return Interpol.Some(
            some.string,
            visitExpr(some.expr),
            visitInterpol(some.next),
            FromInfo(some.info).infer(from = some.wrap(), with = infer)
        )
    }

    private fun visitEnd(end: Interpol.End): Interpol.End {
        return Interpol.End(
            end.string,
            FromInfo(end.info).infer(from = end.wrap(), with = infer)
        )
    }
}