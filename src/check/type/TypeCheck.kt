package check.type

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.operator.CompOperator
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.unop.UnOp
import check.help.Assume
import err.note.Note
import type.Type
import type.prelude.PreludeTypes
import util.extension.map
import util.extension.unpacked
import visit.Visit
import ast.info.Info

class TypeCheck : Visit<Program, Boolean> {
    override fun visit(what: Program): Boolean {
        return what.decls.map(::visitDecl).all { it }
    }

    private fun visitDecl(decl: Decl) = when (decl) {
        is Decl.OfStmt -> visitStmt(decl.stmt)
    }

    private fun visitStmt(stmt: Stmt) = when (stmt) {
        is Stmt.Print -> visitPrint(stmt)
        is Stmt.OfExpr -> visitExpr(stmt.expr)
    }

    private fun visitPrint(print: Stmt.Print): Boolean {
        return visitExpr(print.expr)
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
        if (anyIsIgnorable(show, show.expr)) {
            return err()
        }

        return okay()
    }

    private fun visitParens(parens: Expr.Parens): Boolean {
        if (anyIsIgnorable(parens, parens.expr)) {
            return err()
        }

        return okay()
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitNeg(unOp)
        is UnOp.Not -> visitNot(unOp)
    }

    private fun visitNeg(neg: UnOp.Neg): Boolean {
        // TODO: add support for idea share checks once ideas are implemented
        if (anyIsIgnorable(neg.wrap(), neg.expr)) {
            return err()
        }

        return Assume.all(neg.expr.info()).are(
            PreludeTypes.INT,
            PreludeTypes.FLOAT
        ).orFail(
            Note.info(loc = neg.op, msg = "only applies to Int or Float at the AST level")
        )
    }

    private fun visitNot(not: UnOp.Not): Boolean {
        // TODO: add support for idea share checks once ideas are implemented
        if (anyIsIgnorable(not.wrap(), not.expr)) {
            return err()
        }

        return Assume.all(not.expr.info()).are(
            PreludeTypes.BOOL
        ).orFail(
            Note.info(loc = not.op, msg = "only applies to Bool at the AST level")
        )
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> visitBitwise(binOp)
        is BinOp.Arith -> visitArith(binOp)
        is BinOp.Comp -> visitComp(binOp)
        is BinOp.Concat -> visitConcat(binOp)
    }

    private fun visitBitwise(bitwise: BinOp.Bitwise): Boolean {
        if (anyIsIgnorable(bitwise.wrap(), bitwise.right, bitwise.left)) {
            return err()
        }

        val operands = bitwise.operands().map(Expr::info)
        return Assume.all(*operands.unpacked()).are(
            PreludeTypes.INT
        ).orFail(
            Note.info(loc = bitwise.op, msg = "only apply to Int at the AST level")
        )
    }

    private fun visitArith(arith: BinOp.Arith): Boolean {
        if (anyIsIgnorable(arith.wrap(), arith.left, arith.right)) {
            return err()
        }

        val operands = arith.operands().map(Expr::info)
        return Assume.sameType(*operands.unpacked()).are(
            PreludeTypes.INT,
            PreludeTypes.FLOAT
        ).orFail(
            Note.info(
                loc = arith.op,
                msg = "only applies to Int or Float at the AST level, when both types match"
            )
        )
    }

    private fun visitComp(comp: BinOp.Comp): Boolean {
        if (anyIsIgnorable(comp.wrap(), comp.left, comp.right)) {
            return err()
        }

        return when (comp.operator) {
            CompOperator.EQ, CompOperator.NEQ -> visitEq(comp)
            else -> visitTrueComp(comp)
        }
    }

    private fun visitEq(comp: BinOp.Comp): Boolean {
        val operands = comp.operands().map(Expr::info)

        return Assume.sameType(*operands.unpacked()).orFail(
            Note.info(loc = comp.op, msg = "only apply to values of the same type at the AST level")
        )
    }

    private fun visitTrueComp(comp: BinOp.Comp): Boolean {
        val operands = comp.operands().map(Expr::info)

        return Assume.sameType(*operands.unpacked()).are(
            PreludeTypes.INT,
            PreludeTypes.FLOAT
        ).orFail(
            Note.info(
                loc = comp.op,
                msg = "only applies to Int or Float at the AST level, when both types match"
            )
        )
    }

    private fun visitConcat(concat: BinOp.Concat): Boolean {
        if (anyIsIgnorable(concat.wrap(), concat.left, concat.right)) {
            return err()
        }

        val operands = concat.operands().map(Expr::info)
        return Assume.sameType(*operands.unpacked()).are(
            PreludeTypes.STR,
            // TODO: add List
        ).orFail(
            Note.info(loc = concat.op, msg = "only applies to Str or List at the AST level")
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

    private fun visitInt(int: Lit.IntLit): Boolean {
        return !int.type().isIgnorable()
    }

    private fun visitFloat(float: Lit.FloatLit): Boolean {
        return !float.type().isIgnorable()
    }

    private fun visitBool(bool: Lit.BoolLit): Boolean {
        return !bool.type().isIgnorable()
    }

    private fun visitStr(str: Lit.StrLit): Boolean {
        return !str.type().isIgnorable()
    }

    private fun visitTable(table: Lit.TableLit): Boolean {
        val (keys, vals) = table.keys.toTypedArray() to table.vals.toTypedArray()

        if (anyIsIgnorable(table.wrap(), *keys, *vals)) {
            return err()
        }

        val (keyInfos, valInfos) = keys.map(Expr::info) to vals.map(Expr::info)
        return visitEntries(keyInfos, valInfos)
    }

    private fun visitEntries(keys: List<Info>, vals: List<Info>): Boolean {
        val (firstKey, firstVal) = keys.first() to vals.first()

        return Assume(keys).are(
            firstKey.type()
        ).orFail(
            Note.info(loc = firstKey.loc(), msg = "first key: ${firstKey.type().named()}")
        ).and(
            Assume(vals).are(
                firstVal.type()
            ).orFail(
                Note.info(loc = firstVal.loc(), msg = "first value: ${firstVal.type().named()}")
            )
        )
    }

    private fun visitNil(nil: Lit.NilLit): Boolean {
        return !nil.type().isIgnorable()
    }

    private fun visitInterpol(interpol: Interpol) = when (interpol) {
        is Interpol.Some -> visitSome(interpol)
        is Interpol.End -> visitEnd(interpol)
    }

    private fun visitSome(some: Interpol.Some): Boolean {
        if (anyIsIgnorable(some.wrap(), some.expr, some.next.wrap())) {
            return err()
        }

        // TODO: check whether some.expr shares Show
        return okay()
    }

    private fun visitEnd(end: Interpol.End): Boolean {
        return !end.type().isIgnorable()
    }

    private fun visitEmpty(empty: Expr.Empty): Boolean {
        return err()
    }

    private fun anyIsIgnorable(parent: Expr, vararg exprs: Expr): Boolean {
        if (parent.type().isIgnorable()) {
            return true
        }

        if (exprs.map(::visitExpr).any { !it }) {
            return true
        }

        return exprs.map(Expr::type).any(Type::isIgnorable)
    }

    private fun okay(): Boolean {
        return true
    }

    private fun err(): Boolean {
        return false
    }
}