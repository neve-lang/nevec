package transpile

import ast.hierarchy.binop.operator.ArithOperator
import ast.hierarchy.binop.operator.BitwiseOperator
import ast.hierarchy.binop.operator.CompOperator
import ast.hierarchy.binop.operator.ConcatOperator
import csr.hierarchy.binop.CsrBinOp
import csr.hierarchy.expr.CsrExpr
import csr.hierarchy.lit.CsrLit
import csr.hierarchy.program.CsrProgram
import csr.hierarchy.top.CsrTop
import csr.hierarchy.unop.CsrUnOp
import ctx.Ctx
import nevec.result.Aftermath
import stage.Stage
import type.Type
import type.prelude.PreludeTypes

/**
 * Transpiles the CSR (Caml-suited representation) down to OCaml source code.
 */
class Transpile : Stage<CsrProgram, String> {
    private val builder = StringBuilder()

    override fun perform(data: CsrProgram, ctx: Ctx): Aftermath<String> {
        data.decls.forEach(::visitTop)

        return builder.toString().let {
            Aftermath.Success(it)
        }
    }

    private fun visitTop(top: CsrTop) = when (top) {
        is CsrTop.MainFun -> visitMainFun(top)
        is CsrTop.Fun -> visitFun(top)
    }

    private fun visitMainFun(mainFun: CsrTop.MainFun) {
        builder.appendLine("let main () =")

        indent {
            visitExpr(mainFun.body)
        }

        builder.appendLine()
        builder.appendLine("let () = main ()")
    }

    private fun visitFun(topFun: CsrTop.Fun) {
        builder.appendLine("let ${topFun.name} () =")

        indent {
            visitExpr(topFun.body)
        }
    }

    private fun visitExpr(expr: CsrExpr) = when (expr) {
        is CsrExpr.Print -> visitPrint(expr)
        is CsrExpr.Parens -> visitParens(expr)
        is CsrExpr.OfUnOp -> visitUnOp(expr.unOp)
        is CsrExpr.OfLit -> visitLit(expr.lit)
        is CsrExpr.OfBinOp -> visitBinOp(expr.binOp)
    }

    private fun visitPrint(print: CsrExpr.Print) {
        builder.append(when (print.expr.type()) {
            PreludeTypes.INT -> "Printf.printf \"%d\\n\" "
            PreludeTypes.FLOAT -> "Printf.printf \"%.14g\\n\" "
            PreludeTypes.STR -> "print_endline "
            PreludeTypes.BOOL -> "Printf.printf \"%b\\n\" "

            // silly emulation
            PreludeTypes.NIL -> "print_endline \"nil\" "

            else -> throw UnsupportedOperationException("Unsupported type in `Transpile.visitPrint`")
        })

        if (!print.expr.type().isSame(PreludeTypes.NIL)) {
            parenthesized {
                visitExpr(print.expr)
            }
        }
    }

    private fun visitParens(parens: CsrExpr.Parens) {
        parenthesized {
            visitExpr(parens.expr)
        }
    }

    private fun visitUnOp(unOp: CsrUnOp) = when (unOp) {
        is CsrUnOp.Neg -> visitNeg(unOp)
        is CsrUnOp.Not -> visitNot(unOp)
    }

    private fun visitLit(lit: CsrLit) = when (lit) {
        is CsrLit.CsrInt -> visitInt(lit)
        is CsrLit.CsrFloat -> visitFloat(lit)
        is CsrLit.CsrBool -> visitBool(lit)
        is CsrLit.CsrStr -> visitStr(lit)
        is CsrLit.CsrNil -> visitNil(lit)
    }

    private fun visitInt(int: CsrLit.CsrInt) {
        builder.append(int.value)
    }

    private fun visitFloat(float: CsrLit.CsrFloat) {
        builder.append(float.value)
    }

    private fun visitBool(bool: CsrLit.CsrBool) {
        builder.append(bool.value)
    }

    private fun visitStr(str: CsrLit.CsrStr) {
        builder.append(str.value)
    }

    private fun visitNil(nil: CsrLit.CsrNil) {
        builder.append("None")
    }

    private fun visitNeg(neg: CsrUnOp.Neg) {
        val dot = dotIfFloatingPointArithmetic(neg.operand.type())
        builder.append("~-$dot")

        visitExpr(neg.operand)
    }

    private fun visitNot(not: CsrUnOp.Not) {
        builder.append("not ")
        visitExpr(not.operand)
    }

    private fun visitBinOp(binOp: CsrBinOp) = when (binOp) {
        is CsrBinOp.Bitwise -> visitBitwise(binOp)
        is CsrBinOp.Comp -> visitComp(binOp)
        is CsrBinOp.Concat -> visitConcat(binOp)
        is CsrBinOp.Arith -> visitArith(binOp)
    }

    private fun visitBitwise(bitwise: CsrBinOp.Bitwise) {
        visitExpr(bitwise.left)

        builder.append(when (bitwise.operator) {
            BitwiseOperator.SHR -> " lsr "
            BitwiseOperator.SHL -> " lsl "
            BitwiseOperator.BIT_AND -> " land "
            BitwiseOperator.BIT_OR -> " lor "
            BitwiseOperator.BIT_XOR -> " lxor "
        })

        visitExpr(bitwise.right)
    }

    private fun visitComp(comp: CsrBinOp.Comp) {
        visitExpr(comp.left)

        builder.append(when (comp.operator) {
            CompOperator.EQ -> " = "
            CompOperator.NEQ -> " <> "
            CompOperator.GT -> " > "
            CompOperator.GTE -> " >= "
            CompOperator.LT -> " < "
            CompOperator.LTE -> " <= "
        })

        visitExpr(comp.right)
    }

    private fun visitConcat(concat: CsrBinOp.Concat) {
        visitExpr(concat.left)

        builder.append(when (concat.operator) {
            ConcatOperator.STR -> " ^ "
            ConcatOperator.LIST -> " @ "
        })

        visitExpr(concat.right)
    }

    private fun visitArith(arith: CsrBinOp.Arith) {
        visitExpr(arith.left)

        val dot = dotIfFloatingPointArithmetic(arith.type)
        builder.append(when (arith.operator) {
            ArithOperator.ADD -> " +"
            ArithOperator.SUB -> " -"
            ArithOperator.MUL -> " *"
            ArithOperator.DIV -> " /"
        } + dot)

        visitExpr(arith.right)
    }

    private fun dotIfFloatingPointArithmetic(type: Type): String {
        return if (type.isSame(PreludeTypes.FLOAT))
            ". "
        else
            " "
    }

    private fun parenthesized(callback: () -> Unit) {
        builder.append("(")
        callback()
        builder.append(")")
    }

    private fun indent(callback: () -> Unit) {
        builder.append("  ")
        callback()
    }
}