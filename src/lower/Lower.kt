package lower

import ast.hierarchy.binop.BinOp
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.top.Top
import ast.hierarchy.unop.UnOp
import csr.hierarchy.binop.CsrBinOp
import csr.hierarchy.expr.CsrExpr
import csr.hierarchy.lit.CsrLit
import csr.hierarchy.program.CsrProgram
import csr.hierarchy.top.CsrTop
import csr.hierarchy.unop.CsrUnOp
import csr.type.CsrType
import csr.type.applied.CsrApplied
import csr.type.atom.CsrAtom
import ctx.Ctx
import nevec.result.Aftermath
import stage.Stage
import type.Type
import type.gen.Applied
import type.kind.TypeKind
import util.extension.map

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
        is Top.Empty -> throw IllegalArgumentException(
            "Empty AST node at Lower stage."
        )
    }

    private fun visitFun(topFun: Top.Fun): CsrTop {
        val last = topFun.decls.last()
        val expr = topFun.decls
            .dropLast(1)
            .foldRight(visitDecl(last)) {
                decl, acc -> CsrExpr.In(
                    // in the future, we will make the LetUnit(..) wrapping conditional
                    left = CsrExpr.LetUnit(visitDecl(decl)),
                    right = acc
                )
            }

        return if (isMainFun(topFun))
            CsrTop.MainFun(expr)
        else
            CsrTop.Fun(topFun.name, expr, visitType(expr.type()))
    }

    private fun visitDecl(decl: Decl) = when (decl) {
        is Decl.OfStmt -> visitStmt(decl.stmt)
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
        is Expr.OfUnOp -> visitUnOp(expr.unOp)
        is Expr.OfLit -> visitLit(expr.lit)
        is Expr.OfBinOp -> visitBinOp(expr.binOp)

        is Expr.Show, is Expr.Empty, is Expr.OfInterpol -> throw UnsupportedOperationException(
            "Expr.Show, Expr.Empty and Expr.OfInterpol are not supported yet."
        )
    }

    private fun visitParens(expr: Expr.Parens): CsrExpr.Parens {
        return CsrExpr.Parens(visitExpr(expr.expr))
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitNeg(unOp)
        is UnOp.Not -> visitNot(unOp)
    }

    private fun visitNeg(neg: UnOp.Neg): CsrExpr.OfUnOp {
        return CsrUnOp.Neg(visitExpr(neg.expr)).let {
            CsrExpr.OfUnOp(it, neg.type())
        }
    }

    private fun visitNot(not: UnOp.Not): CsrExpr.OfUnOp {
        return CsrUnOp.Not(visitExpr(not.expr)).let {
            CsrExpr.OfUnOp(it, not.type())
        }
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> visitBitwise(binOp)
        is BinOp.Arith -> visitArith(binOp)
        is BinOp.Comp -> visitComp(binOp)
        is BinOp.Concat -> visitConcat(binOp)
    }

    private fun visitBitwise(bitwise: BinOp.Bitwise): CsrExpr.OfBinOp {
        val (left, right) = bitwise.operands().map(::visitExpr)

        return CsrBinOp.Bitwise(left, bitwise.operator, right, bitwise.type()).let {
            CsrExpr.OfBinOp(it, bitwise.type())
        }
    }

    private fun visitArith(arith: BinOp.Arith): CsrExpr.OfBinOp {
        val (left, right) = arith.operands().map(::visitExpr)

        return CsrBinOp.Arith(left, arith.operator, right, arith.type()).let {
            CsrExpr.OfBinOp(it, arith.type())
        }
    }

    private fun visitComp(comp: BinOp.Comp): CsrExpr.OfBinOp {
        val (left, right) = comp.operands().map(::visitExpr)

        return CsrBinOp.Comp(left, comp.operator, right, comp.type()).let {
            CsrExpr.OfBinOp(it, comp.type())
        }
    }

    private fun visitConcat(concat: BinOp.Concat): CsrExpr.OfBinOp {
        val (left, right) = concat.operands().map(::visitExpr)

        return CsrBinOp.Concat(left, concat.operator, right, concat.type()).let {
            CsrExpr.OfBinOp(it, concat.type())
        }
    }

    private fun visitLit(lit: Lit) = when (lit) {
        is Lit.BoolLit -> visitBool(lit)
        is Lit.FloatLit -> visitFloat(lit)
        is Lit.IntLit -> visitInt(lit)
        is Lit.StrLit -> visitStr(lit)
        is Lit.NilLit -> visitNil(lit)
        is Lit.TableLit -> throw UnsupportedOperationException(
            "Neve tables will no longer be a primitive type in future development versions."
        )
    }

    private fun visitBool(bool: Lit.BoolLit): CsrExpr.OfLit {
        return CsrLit.CsrBool(bool.value).let {
            CsrExpr.OfLit(it, bool.type())
        }
    }

    private fun visitFloat(float: Lit.FloatLit): CsrExpr.OfLit {
        return CsrLit.CsrFloat(float.value).let {
            CsrExpr.OfLit(it, float.type())
        }
    }

    private fun visitInt(int: Lit.IntLit): CsrExpr.OfLit {
        return CsrLit.CsrInt(int.value).let {
            CsrExpr.OfLit(it, int.type())
        }
    }

    private fun visitStr(str: Lit.StrLit): CsrExpr.OfLit {
        return CsrLit.CsrStr(str.value).let {
            CsrExpr.OfLit(it, str.type())
        }
    }

    private fun visitNil(nil: Lit.NilLit): CsrExpr.OfLit {
        return CsrExpr.OfLit(CsrLit.CsrNil, nil.type())
    }

    private fun visitType(type: Type) = when (val kind = type.kind) {
        is TypeKind.OfApplied -> visitApplied(kind.applied)
        else -> visitAtom(kind)
    }

    private fun visitApplied(applied: Applied): CsrType.Applied {
        return CsrApplied(
            visitType(applied.type),
            applied.args.themselves().map { visitType(it) }
        ).let {
            CsrType.Applied(it)
        }
    }

    private fun visitAtom(kind: TypeKind): CsrType.Atom {
        return CsrAtom(kind.named()).let { CsrType.Atom(it) }
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