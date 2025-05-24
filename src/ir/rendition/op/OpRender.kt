package ir.rendition.op

import ir.rendition.names.Names
import ir.structure.consts.IrConst
import ir.structure.op.Op
import ir.structure.tac.Tac
import ir.term.TermLike
import util.extension.infixWith
import util.extension.wrappedIn

/**
 * Takes care of pretty-printing IR [Ops][Op].
 */
class OpRender<T : TermLike>(private val termNames: Names) {
    /**
     * @return A [String] containing the pretty-printed version of [op].
     */
    fun render(op: Op<T>) = when (op) {
        is Op.Ret -> renderRet(op)
        is Op.Print -> renderPrint(op)
        is Op.Const -> renderConst(op)
        is Op.OfTac -> renderTac(op.tac)
    }

    private fun renderRet(op: Op.Ret<T>): String {
        return "ret ${nameOf(op.term)}"
    }

    private fun renderPrint(op: Op.Print<T>): String {
        return "print ${nameOf(op.term)}"
    }

    private fun renderConst(op: Op.Const<T>): String {
        return "${nameOf(op.to)} = ${renderIrConst(op.const)}"
    }

    private fun renderTac(tac: Tac<T>) = when (tac) {
        is Tac.Concat -> renderConcat(tac)
        is Tac.Show -> renderShow(tac)
        is Tac.Neg -> renderNeg(tac)
        is Tac.Not -> renderNot(tac)
        is Tac.Eq -> renderEq(tac)
        is Tac.Neq -> renderNeq(tac)
        is Tac.Gt -> renderGt(tac)
        is Tac.Gte -> renderGte(tac)
        is Tac.Lt -> renderLt(tac)
        is Tac.Lte -> renderLte(tac)
        is Tac.Shl -> renderShl(tac)
        is Tac.Shr -> renderShr(tac)
        is Tac.BitAnd -> renderBitAnd(tac)
        is Tac.BitOr -> renderBitOr(tac)
        is Tac.BitXor -> renderBitXor(tac)
        is Tac.Add -> renderAdd(tac)
        is Tac.Sub -> renderSub(tac)
        is Tac.Mul -> renderMul(tac)
        is Tac.Div -> renderDiv(tac)
        is Tac.TableSet -> renderTableSet(tac)
    }

    private fun renderConcat(concat: Tac.Concat<T>): String {
        return renderBinOp(concat.to, concat.left, "concat", concat.right)
    }

    private fun renderShow(show: Tac.Show<T>): String {
        return "${nameOf(show.to)} = show ${nameOf(show.term)}"
    }

    private fun renderNeg(neg: Tac.Neg<T>): String {
        return "${nameOf(neg.to)} = -${nameOf(neg.term)}"
    }

    private fun renderNot(not: Tac.Not<T>): String {
        return "${nameOf(not.to)} = not ${nameOf(not.term)}"
    }

    private fun renderEq(eq: Tac.Eq<T>): String {
        return renderBinOp(eq.to, eq.left, op = "==", eq.right)
    }

    private fun renderNeq(neq: Tac.Neq<T>): String {
        return renderBinOp(neq.to, neq.left, op = "!=", neq.right)
    }

    private fun renderGt(gt: Tac.Gt<T>): String {
        return renderBinOp(gt.to, gt.left, op = ">", gt.right)
    }

    private fun renderGte(gte: Tac.Gte<T>): String {
        return renderBinOp(gte.to, gte.left, op = ">=", gte.right)
    }

    private fun renderLt(lt: Tac.Lt<T>): String {
        return renderBinOp(lt.to, lt.left, op = "<", lt.right)
    }

    private fun renderLte(lte: Tac.Lte<T>): String {
        return renderBinOp(lte.to, lte.left, op = "<=", lte.right)
    }

    private fun renderShl(shl: Tac.Shl<T>): String {
        return renderBinOp(shl.to, shl.left, op = "<<", shl.right)
    }

    private fun renderShr(shr: Tac.Shr<T>): String {
        return renderBinOp(shr.to, shr.left, op = ">>", shr.right)
    }

    private fun renderBitAnd(bitAnd: Tac.BitAnd<T>): String {
        return renderBinOp(bitAnd.to, bitAnd.left, op = "bitand", bitAnd.right)
    }

    private fun renderBitOr(bitOr: Tac.BitOr<T>): String {
        return renderBinOp(bitOr.to, bitOr.left, op = "bitor", bitOr.right)
    }

    private fun renderBitXor(bitXor: Tac.BitXor<T>): String {
        return renderBinOp(bitXor.to, bitXor.left, op = "xor", bitXor.right)
    }

    private fun renderAdd(add: Tac.Add<T>): String {
        return renderBinOp(add.to, add.left, op = "+", add.right)
    }

    private fun renderSub(sub: Tac.Sub<T>): String {
        return renderBinOp(sub.to, sub.left, op = "-", sub.right)
    }

    private fun renderMul(mul: Tac.Mul<T>): String {
        return renderBinOp(mul.to, mul.left, op = "*", mul.right)
    }

    private fun renderDiv(div: Tac.Div<T>): String {
        return renderBinOp(div.to, div.left, op = "/", div.right)
    }

    private fun renderTableSet(tableSet: Tac.TableSet<T>): String {
        return "${nameOf(tableSet.to)}[${nameOf(tableSet.key)}] = ${nameOf(tableSet.value)}"
    }

    private fun renderIrConst(const: IrConst) = when (const) {
        is IrConst.OfNil -> renderNil(const)
        is IrConst.OfStr -> renderStr(const)
        is IrConst.OfEmptyTable -> renderEmptyTable(const)
        is IrConst.OfBool -> renderBool(const)
        is IrConst.OfInt -> renderInt(const)
        is IrConst.OfFloat -> renderFloat(const)
        is IrConst.OfTable -> renderTable(const)
    }

    private fun renderNil(nil: IrConst.OfNil): String {
        return "nil"
    }

    private fun renderStr(string: IrConst.OfStr): String {
        return string.value
    }

    private fun renderEmptyTable(emptyTable: IrConst.OfEmptyTable): String {
        return "[:]"
    }

    private fun renderBool(bool: IrConst.OfBool): String {
        return bool.value.toString()
    }

    private fun renderInt(int: IrConst.OfInt): String {
        return int.value.toString()
    }

    private fun renderFloat(float: IrConst.OfFloat): String {
        return float.value.toString()
    }

    private fun renderTable(table: IrConst.OfTable): String {
        val keys = table.keys.map(::renderIrConst)
        val vals = table.vals.map(::renderIrConst)

        return (keys zip vals).map { it.infixWith(": ") }.wrappedIn("[", "]").joinToString(", ")
    }

    private fun renderBinOp(receiver: T, left: T, op: String, right: T): String {
        return "${nameOf(receiver)} = ${nameOf(left)} $op ${nameOf(right)}"
    }

    private fun nameOf(term: T): String {
        return termNames.findNameFor(term.id(), term.desiredName())
    }
}