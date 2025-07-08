package opt.passes

import ir.data.change.Change
import ir.data.change.TermChange
import ir.data.`fun`.FunData
import ir.structure.consts.IrConst
import ir.structure.op.Op
import ir.structure.tac.Tac
import ir.term.warm.Warm
import opt.canvas.Canvas
import opt.structure.id.OptId
import opt.structure.pass.Pass
import opt.transform.Transform
import util.extension.*
import util.twice.Twice
import util.twice.twice

/**
 * The constant folding optimization pass.
 *
 * This optimization pass takes in an arithmetic operation-like [Tac] expression and tries to fold it into a single
 * [Op.Const] IR operation.
 *
 * This optimization pass is only applied if both operands are known to be constants—that is, if either their
 * definition is one of [Op.Const], or if their [Domain][domain.Domain] is singleton.
 */
class ConstFold : Pass {
    override fun id(): OptId {
        return OptId.CONST_FOLD
    }

    override fun apply(to: Canvas): Canvas {
        return to.eachOp { _, data, op ->
            if (isArith(op))
                constFoldIfPossible((op as Op.OfTac).tac, data).withChange(
                    TermChange.Unuse(op.allTerms().drop(1), op)
                        .wrap()
                )
            else
                Transform.Retain(op)
        }
    }

    private fun constFoldIfPossible(tac: Tac<Warm>, data: FunData<Warm>): Transform<Op<Warm>> {
        val operands = tac.allTerms().drop(1)
        val defs = operands.mapNotNull { data.defOf(it) }

        if (!areFoldable(defs, data)) {
            return Transform.Retain(tac.wrap())
        }

        return constFold(tac, defs)
    }

    private fun constFold(tac: Tac<Warm>, defs: List<Op<Warm>>): Transform<Op<Warm>> {
        return when (tac) {
            is Tac.Eq -> foldEq(tac, defs.twice())
            is Tac.Neq -> foldNeq(tac, defs.twice())
            is Tac.Gt -> foldGt(tac, defs.twice())
            is Tac.Gte -> foldGte(tac, defs.twice())
            is Tac.Lt -> foldLt(tac, defs.twice())
            is Tac.Lte -> foldLte(tac, defs.twice())
            is Tac.Shr -> foldShr(tac, defs.twice())
            is Tac.Shl -> foldShl(tac, defs.twice())
            is Tac.Concat -> foldConcat(tac, defs.twice())
            is Tac.BitAnd -> foldBitAnd(tac, defs.twice())
            is Tac.BitOr -> foldBitOr(tac, defs.twice())
            is Tac.BitXor -> foldBitXor(tac, defs.twice())
            is Tac.Add -> foldAdd(tac, defs.twice())
            is Tac.Sub -> foldSub(tac, defs.twice())
            is Tac.Mul -> foldMul(tac, defs.twice())
            is Tac.Div -> foldDiv(tac, defs.twice())
            is Tac.Show -> foldShow(tac, defs.first())
            is Tac.Neg -> foldNeg(tac, defs.first())
            is Tac.Not -> foldNot(tac, defs.first())

            is Tac.TableSet -> throw IllegalArgumentException(
                "`Tac.TableSet` cannot be constant folded."
            )
        }
    }

    private fun foldEq(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        assertConsts(defs)

        val folded = constValues(defs)
            .let { (a, b) -> a == b }
            .let { IrConst.OfBool(it) }

        return newConst(from = tac, folded)
    }

    private fun foldNeq(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs)
            .let { (a, b) -> a != b }
            .let { IrConst.OfBool(it) }

        return newConst(from = tac, folded)
    }

    private fun foldGt(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a > b }
            .let { IrConst.OfBool(it) }

        return newConst(from = tac, folded)
    }

    private fun foldGte(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a >= b }
            .let { IrConst.OfBool(it) }

        return newConst(from = tac, folded)
    }

    private fun foldLt(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a < b }
            .let { IrConst.OfBool(it) }

        return newConst(from = tac, folded)
    }

    private fun foldLte(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a <= b }
            .let { IrConst.OfBool(it) }

        return newConst(from = tac, folded)
    }

    private fun foldConcat(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfStr }
            .map { (it as IrConst.OfStr).value.trimQuotesAround()  }
            .let { (a, b) -> a + b }
            .wrappedInQuotes()
            .let { IrConst.OfStr(it) }

        return newConst(from = tac, folded)
    }

    private fun foldBitOr(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a or b }
            .let { IrConst.OfInt(it) }

        return newConst(from = tac, folded)
    }

    private fun foldBitAnd(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a and b }
            .let { IrConst.OfInt(it) }

        return newConst(from = tac, folded)
    }

    private fun foldBitXor(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a xor b }
            .let { IrConst.OfInt(it) }

        return newConst(from = tac, folded)
    }

    private fun foldShl(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a shl b }
            .let { IrConst.OfInt(it) }

        return newConst(from = tac, folded)
    }

    private fun foldShr(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt }
            .map { (it as IrConst.OfInt).value  }
            .let { (a, b) -> a shr b }
            .let { IrConst.OfInt(it) }

        return newConst(from = tac, folded)
    }

    private fun foldMul(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt || it is IrConst.OfFloat }
            .let { (a, b) ->
                when (a) {
                    is IrConst.OfInt -> IrConst.OfInt(a.value * (b as IrConst.OfInt).value)
                    is IrConst.OfFloat -> IrConst.OfFloat(a.value * (b as IrConst.OfFloat).value)

                    else -> throw IllegalArgumentException("Unexpected IrConst kind in multiplication.")
                }
            }

        return newConst(from = tac, folded)
    }

    private fun foldDiv(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt || it is IrConst.OfFloat }
            .let { (a, b) ->
                when (a) {
                    is IrConst.OfInt -> IrConst.OfInt(a.value / (b as IrConst.OfInt).value)
                    is IrConst.OfFloat -> IrConst.OfFloat(a.value / (b as IrConst.OfFloat).value)

                    else -> throw IllegalArgumentException("Unexpected IrConst kind in multiplication.")
                }
            }

        return newConst(from = tac, folded)
    }

    private fun foldAdd(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt || it is IrConst.OfFloat }
            .let { (a, b) ->
                when (a) {
                    is IrConst.OfInt -> IrConst.OfInt(a.value + (b as IrConst.OfInt).value)
                    is IrConst.OfFloat -> IrConst.OfFloat(a.value + (b as IrConst.OfFloat).value)

                    else -> throw IllegalArgumentException("Unexpected IrConst kind in multiplication.")
                }
            }

        return newConst(from = tac, folded)
    }

    private fun foldSub(tac: Tac<Warm>, defs: Twice<Op<Warm>>): Transform<Op<Warm>> {
        val folded = operands(defs) { it is IrConst.OfInt || it is IrConst.OfFloat }
            .let { (a, b) ->
                when (a) {
                    is IrConst.OfInt -> IrConst.OfInt(a.value - (b as IrConst.OfInt).value)
                    is IrConst.OfFloat -> IrConst.OfFloat(a.value - (b as IrConst.OfFloat).value)

                    else -> throw IllegalArgumentException("Unexpected IrConst kind in multiplication.")
                }
            }

        return newConst(from = tac, folded)
    }

    private fun foldShow(tac: Tac<Warm>, operand: Op<Warm>): Transform<Op<Warm>> {
        assert(operand is Op.Const) {
            "Unexpected non-constant in constant folding pass."
        }

        val folded = foldShowFormat((operand as Op.Const).const)
            // first trim the quotes if it's a string...
            .trimQuotesAround()
            // ...  then wrap it in quotes so the quotes are there if `operand` is not a string
            .wrappedInQuotes()
            .let { IrConst.OfStr(it) }

        return newConst(from = tac, folded)
    }

    private fun foldNeg(tac: Tac<Warm>, operand: Op<Warm>): Transform<Op<Warm>> {
        assert(
        operand is Op.Const &&
                (operand.const is IrConst.OfInt || operand.const is IrConst.OfFloat)
        ) {
            "Unexpected invalid constant in constant folding pass."
        }

        val folded = when (val const = (operand as Op.Const).const) {
            is IrConst.OfInt -> IrConst.OfInt(-const.value)
            is IrConst.OfFloat -> IrConst.OfFloat(-const.value)

            else -> throw IllegalArgumentException(
                "Unexpected invalid constant in constant folding pass."
            )
        }

        return newConst(from = tac, folded)
    }

    private fun foldNot(tac: Tac<Warm>, operand: Op<Warm>): Transform<Op<Warm>> {
        assert(operand is Op.Const && operand.const is IrConst.OfBool) {
            "Unexpected invalid constant in constant folding pass."
        }

        val const = (operand as Op.Const).const
        val folded = (const as IrConst.OfBool)
            .let { IrConst.OfBool(!it.value) }

        return newConst(from = tac, folded)
    }

    private fun foldShowFormat(const: IrConst): String {
        return when (const) {
            is IrConst.OfInt -> const.value.toString()
            is IrConst.OfFloat -> String.format("%.14g", const.value)
            is IrConst.OfStr -> const.value
            is IrConst.OfBool -> const.value.toString()
            is IrConst.OfNil -> "nil"
            is IrConst.OfEmptyTable -> "[:]"
            is IrConst.OfTable -> foldTableShowFormat(const)
        }
    }

    private fun foldTableShowFormat(table: IrConst.OfTable): String {
        val keys = table.keys.map(::foldShowFormat)
        val vals = table.vals.map(::foldShowFormat)

        return (keys zip vals)
            .map { it.infixWith(": ") }
            .wrappedIn("[", "]")
            .joinToString(", ")
    }

    private fun operands(
        defs: Twice<Op<Warm>>,
        assertion: (IrConst) -> Boolean = { true }
    ): Twice<IrConst> {
        assertConsts(defs)

        val values = constValues(defs)
        assert(values.all(assertion))

        return values
    }

    private fun assertConsts(defs: Twice<Op<Warm>>) {
        assert(
            defs.all { it is Op.Const } &&
            defs.let { (a, b) -> a::class == b::class }
        ) {
            "Unexpected non-constants in constant folding pass."
        }
    }

    private fun constValues(defs: Twice<Op<Warm>>): Twice<IrConst> {
        return defs.map { (it as Op.Const).const }
    }

    private fun newConst(from: Tac<Warm>, const: IrConst): Transform<Op<Warm>> {
        val constOp = Op.Const(
            from.term(),
            const,
            from.info()
        )

        val operands = from.allTerms().drop(1)

        return Transform.Replace(
            new = constOp,
            Change.deriveFrom(constOp)
        )
    }

    private fun areFoldable(defs: List<Op<Warm>>, data: FunData<Warm>): Boolean {
        return defs.isNotEmpty() && defs.all { isFoldable(it, data) }
    }

    private fun isFoldable(def: Op<Warm>, data: FunData<Warm>): Boolean {
        if (def !is Op.Const) {
            return false
        }

        return when (def.const) {
            is IrConst.OfTable, IrConst.OfEmptyTable -> isTableFoldable(def, data)
            else -> true
        }
    }

    private fun isTableFoldable(def: Op<Warm>, data: FunData<Warm>): Boolean {
        return data
            .usesOf(def.term())
            .none {
                it is Op.OfTac && it.tac is Tac.TableSet
            }
    }

    private fun isArith(op: Op<Warm>): Boolean {
        return when (op) {
            is Op.OfTac -> isTacArith(op.tac)
            else -> false
        }
    }

    private fun isTacArith(tac: Tac<Warm>): Boolean {
        return when (tac) {
            is Tac.TableSet -> false
            else -> true
        }
    }
}