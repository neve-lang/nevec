package infer

import ast.hierarchy.binop.BinOp
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.unop.UnOp
import infer.gen.GenTracker
import infer.unify.Unify
import type.Type
import type.gen.Applied
import type.gen.arg.TypeArgs
import type.prelude.PreludeTypes

/**
 * Performs type inference on [Expr] nodes.
 *
 * Works alongside [SemResolver][check.sem.SemResolver] and uses [Unify].
 *
 * Because [Infer] works alongside [SemResolver][check.sem.SemResolver], it actually **does not perform type inference
 * recursively**, because **the recursion is already done by the semantic resolver**.
 *
 * @see Unify
 * @see check.sem.SemResolver
 */
class Infer {
    private val gens = GenTracker()

    fun visit(expr: Expr) = when (expr) {
        is Expr.Show -> visitShow(expr)
        is Expr.Parens -> visitParens(expr)

        is Expr.OfUnOp -> visitUnOp(expr.unOp)
        is Expr.OfBinOp -> visitBinOp(expr.binOp)
        is Expr.OfLit -> visitLit(expr.lit)
        is Expr.OfInterpol -> visitInterpol(expr.interpol)

        is Expr.Empty -> Type.unknown()
    }

    private fun visitShow(show: Expr.Show): Type {
        return PreludeTypes.STR
    }

    private fun visitParens(parens: Expr.Parens): Type {
        return parens.expr.type()
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitNeg(unOp)
        is UnOp.Not -> visitNot(unOp)
    }

    private fun visitNeg(neg: UnOp.Neg): Type {
        // TODO: implement domain evolution
        return neg.expr.type()
    }

    private fun visitNot(not: UnOp.Not): Type {
        // TODO: implement domain evolution
        return not.expr.type()
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> visitBitwise(binOp)
        is BinOp.Arith -> visitArith(binOp)
        is BinOp.Comp -> visitComp(binOp)
        is BinOp.Concat -> visitConcat(binOp)
    }

    private fun visitBitwise(bitwise: BinOp.Bitwise): Type {
        return Unify.both(bitwise.operands()).assuming(PreludeTypes.INT)
    }

    private fun visitArith(arith: BinOp.Arith): Type {
        return Unify.both(arith.operands()).assuming(
            PreludeTypes.INT,
            PreludeTypes.FLOAT
        )
    }

    private fun visitComp(comp: BinOp.Comp): Type {
        return Unify.both(comp.operands()).into(PreludeTypes.BOOL)
    }

    private fun visitConcat(concat: BinOp.Concat): Type {
        return Unify.both(concat.operands()).assuming(
            PreludeTypes.STR,
            // TODO: add PreludeTypes.LIST
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

    private fun visitInt(int: Lit.IntLit): Type {
        return PreludeTypes.INT
    }

    private fun visitFloat(float: Lit.FloatLit): Type {
        return PreludeTypes.FLOAT
    }

    private fun visitBool(bool: Lit.BoolLit): Type {
        return PreludeTypes.BOOL
    }

    private fun visitStr(str: Lit.StrLit): Type {
        return PreludeTypes.STR
    }

    private fun visitTable(table: Lit.TableLit): Type {
        if (table.keys.isEmpty()) {
            val args = TypeArgs.frees(gens.order(2).frees())
            return Applied(args, PreludeTypes.TABLE).covered()
        }

        val keys = Unify.all(table.keys.map(Expr::type))
        val values = Unify.all(table.vals.map(Expr::type))

        return Applied(TypeArgs.from(keys, values), PreludeTypes.TABLE).covered()
    }

    private fun visitNil(nil: Lit.NilLit): Type {
        return PreludeTypes.NIL
    }

    private fun visitInterpol(interpol: Interpol): Type {
        return PreludeTypes.STR
    }
}
