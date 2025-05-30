package ir.lower

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.operator.*
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.top.Top
import ast.hierarchy.unop.UnOp
import ast.info.impl.Infoful
import ctx.Ctx
import ir.info.IrInfo
import ir.lower.provide.Blocks
import ir.structure.Ir
import ir.structure.compose.Compose
import ir.structure.compose.Zipping
import ir.structure.consts.IrConst
import ir.structure.op.Op
import ir.structure.tac.Tac
import ir.lower.provide.Terms
import ir.structure.`fun`.IrFun
import ir.term.warm.Warm
import ir.term.warm.Term
import nevec.result.Aftermath
import stage.Stage
import util.extension.map
import util.extension.trimQuotesAround
import util.extension.wrappedInQuotes

/**
 * The AST lowering pass.
 *
 * This phase takes an AST program node and outputs a **warm** [Ir] for the program.
 *
 * @see ir.term.TermLike
 */
class Lower : Stage<Program, Ir<Warm>> {
    private val terms = Terms()
    private val blocks = Blocks()

    override fun perform(data: Program, ctx: Ctx): Aftermath<Ir<Warm>> {
        val functions = data.decls.map(::visitTop)

        return Ir(functions).let {
            Aftermath.Success(it)
        }
    }

    private fun visitTop(top: Top) = when (top) {
        is Top.Fun -> visitFun(top)

        is Top.Empty -> throw IllegalArgumentException(
            "`Top.Empty` declarations may not appear in the lowering phase."
        )
    }

    private fun visitFun(node: Top.Fun): IrFun<Warm> {
        val compose = node.decls.map(::visitDecl).reduce(Compose::merge)

        // TODO: use the mangled name instead
        return IrFun.from(
            listOf(blocks.newBasic(compose.ops())),
            name = node.name,
        )
    }

    private fun visitDecl(decl: Decl) = when (decl) {
        is Decl.OfStmt -> visitStmt(decl.stmt)
    }

    private fun visitStmt(stmt: Stmt) = when (stmt) {
        is Stmt.Print -> visitPrint(stmt)
        is Stmt.OfExpr -> visitExpr(stmt.expr)
    }

    private fun visitPrint(print: Stmt.Print): Compose {
        return visitExpr(print.expr).withLast { expr ->
            Op.Print(
                term = expr,
                info = IrInfo.from(print.loc())
            )
        }
    }

    private fun visitExpr(expr: Expr): Compose = when (expr) {
        is Expr.Show -> visitShow(expr)
        is Expr.Parens -> visitParens(expr)

        is Expr.OfUnOp -> visitUnOp(expr.unOp)
        is Expr.OfBinOp -> visitBinOp(expr.binOp)
        is Expr.OfLit -> visitLit(expr.lit)
        is Expr.OfInterpol -> visitInterpol(expr.interpol)

        is Expr.Empty -> throw IllegalArgumentException(
            "Empty expressions may not appear in the lowering phase."
        )
    }

    private fun visitShow(show: Expr.Show): Compose {
        return visitExpr(show.expr).withLast { expr ->
            Tac.Show(
                to = newTemp(show),
                term = expr,
                info = IrInfo.from(show.loc())
            ).wrap()
        }
    }

    private fun visitParens(parens: Expr.Parens): Compose {
        return visitExpr(parens.expr)
    }

    private fun visitUnOp(unOp: UnOp) = when (unOp) {
        is UnOp.Neg -> visitNeg(unOp)
        is UnOp.Not -> visitNot(unOp)
    }

    private fun visitNeg(neg: UnOp.Neg): Compose {
        return visitExpr(neg.expr).withLast { operand ->
            Tac.Neg(
                to = newTemp(neg),
                term = operand,
                info = IrInfo.from(neg.loc())
            ).wrap()
        }
    }

    private fun visitNot(not: UnOp.Not): Compose {
        return visitExpr(not.expr).withLast { operand ->
            Tac.Not(
                to = newTemp(not),
                term = operand,
                info = IrInfo.from(not)
            ).wrap()
        }
    }

    private fun visitBinOp(binOp: BinOp) = when (binOp) {
        is BinOp.Bitwise -> visitBitwise(binOp)
        is BinOp.Arith -> visitArith(binOp)
        is BinOp.Comp -> visitComp(binOp)
        is BinOp.Concat -> visitConcat(binOp)
    }

    private fun visitBitwise(bitwise: BinOp.Bitwise): Compose {
        val info = IrInfo.from(bitwise)
        val (left, right) = bitwise.operands().map { visitExpr(it) }

        return left.join(right).then { (a, b) ->
            newTemp(bitwise).let {
                when (bitwise.operator) {
                    BitwiseOperator.SHL -> Tac.Shl(it, a, b, info)
                    BitwiseOperator.SHR -> Tac.Shr(it, a, b, info)
                    BitwiseOperator.BIT_AND -> Tac.BitAnd(it, a, b, info)
                    BitwiseOperator.BIT_OR -> Tac.BitOr(it, a, b, info)
                    BitwiseOperator.BIT_XOR -> Tac.BitXor(it, a, b, info)
                }.wrap()
            }
        }.connect()
    }

    private fun visitArith(arith: BinOp.Arith): Compose {
        val info = IrInfo.from(arith)
        val (left, right) = arith.operands().map { visitExpr(it) }

        return left.join(right).then { (a, b) ->
            newTemp(arith).let {
                when (arith.operator) {
                    ArithOperator.ADD -> Tac.Add(it, a, b, info)
                    ArithOperator.SUB -> Tac.Sub(it, a, b, info)
                    ArithOperator.MUL -> Tac.Mul(it, a, b, info)
                    ArithOperator.DIV -> Tac.Div(it, a, b, info)
                }.wrap()
            }
        }.connect()
    }

    private fun visitComp(comp: BinOp.Comp): Compose {
        val info = IrInfo.from(comp)
        val (left, right) = comp.operands().map { visitExpr(it) }

        return left.join(right).then { (a, b) ->
            newTemp(comp).let {
                when (comp.operator) {
                    CompOperator.EQ -> Tac.Eq(it, a, b, info)
                    CompOperator.NEQ -> Tac.Neq(it, a, b, info)
                    CompOperator.GT -> Tac.Gt(it, a, b, info)
                    CompOperator.GTE -> Tac.Gte(it, a, b, info)
                    CompOperator.LT -> Tac.Lt(it, a, b, info)
                    CompOperator.LTE -> Tac.Lte(it, a, b, info)
                }.wrap()
            }
        }.connect()
    }

    private fun visitConcat(concat: BinOp.Concat): Compose {
        val info = IrInfo.from(concat)
        val (left, right) = concat.operands().map { visitExpr(it) }

        return left.join(right).then { (a, b) ->
            newTemp(concat).let {
                when (concat.operator) {
                    ConcatOperator.STR -> Tac.Concat(it, a, b, info)
                    ConcatOperator.LIST -> throw NotImplementedError(
                        "List concatenation has not been implemented yet."
                    )
                }.wrap()
            }
        }.connect()
    }

    private fun visitLit(lit: Lit) = when (lit) {
        is Lit.IntLit -> visitInt(lit)
        is Lit.FloatLit -> visitFloat(lit)
        is Lit.BoolLit -> visitBool(lit)
        is Lit.StrLit -> visitStr(lit)
        is Lit.NilLit -> visitNil(lit)
        is Lit.TableLit -> visitTable(lit)
    }

    private fun visitInt(int: Lit.IntLit): Compose {
        return newConst(from = int, IrConst.OfInt(int.value))
    }

    private fun visitFloat(float: Lit.FloatLit): Compose {
        return newConst(from = float, IrConst.OfFloat(float.value))
    }

    private fun visitBool(bool: Lit.BoolLit): Compose {
        return newConst(from = bool, IrConst.OfBool(bool.value))
    }

    private fun visitStr(str: Lit.StrLit): Compose {
        return newStr(from = str, str.value)
    }

    private fun visitTable(table: Lit.TableLit): Compose {
        val info = IrInfo.from(table)

        val keys = table.keys.map(::visitExpr)
        val vals = table.vals.map(::visitExpr)

        val tableConst = newConst(from = table, IrConst.OfEmptyTable)

        return Zipping(keys, vals).each { (key, value) ->
            Tac.TableSet(
                to = tableConst.term(),
                key,
                value,
                info
            ).wrap()
        }.plugInBetween(tableConst)
    }

    private fun visitNil(nil: Lit.NilLit): Compose {
        return newConst(from = nil, IrConst.OfNil)
    }

    private fun visitInterpol(interpol: Interpol) = when (interpol) {
        is Interpol.Some -> visitSome(interpol)
        is Interpol.End -> visitEnd(interpol)
    }

    private fun visitSome(some: Interpol.Some): Compose {
        val someInfo = IrInfo.from(some)

        val string = newStr(from = some, some.string)

        val showExpr = visitExpr(some.expr).withLast { expr ->
            Tac.Show(
                to = newTemp(some),
                term = expr,
                info = IrInfo.from(some.expr)
            ).wrap()
        }

        val next = visitInterpol(some.next)

        return string.join(showExpr).join(next).then { (a, b) ->
            Tac.Concat(newTemp(some), a, b, someInfo).wrap()
        }.connect().viewing(next).then { (a, b) ->
            Tac.Concat(newTemp(some), a, b, someInfo).wrap()
        }.connect()
    }

    private fun visitEnd(end: Interpol.End): Compose {
        return newStr(from = end, end.string)
    }

    private fun newStr(from: Infoful, str: String): Compose {
        return newConst(
            from,
            IrConst.OfStr(str.trimQuotesAround().wrappedInQuotes())
        )
    }

    private fun newConst(from: Infoful, const: IrConst): Compose {
        return Compose.single(
            Op.Const(
                to = newTemp(from),
                const,
                info = IrInfo.from(from)
            )
        )
    }

    private fun newTemp(basedOn: Infoful): Term {
        return terms.newTemporary(basedOn.type())
    }
}