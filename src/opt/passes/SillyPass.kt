package opt.passes

import ir.data.change.Change
import ir.data.change.TermChange
import ir.data.`fun`.FunData
import ir.provide.IdSystem
import ir.structure.consts.IrConst
import ir.structure.op.Op
import ir.term.warm.Warm
import opt.canvas.Canvas
import opt.structure.id.OptId
import opt.structure.pass.Pass
import opt.transform.Transform

/**
 * A simple optimization pass that replaces all constants of `1` with a `2`, intended for testing.
 */
class SillyPass : Pass {
    override fun id(): OptId {
        TODO("Not yet implemented")
    }

    override fun apply(to: Canvas): Canvas {
        return to.eachOp { _, _, op ->
            when (op) {
                is Op.Const -> replace(op)
                else -> Transform.Retain(op)
            }
        }
    }

    private fun replace(op: Op.Const<Warm>): Transform<Op<Warm>> {
        if (op.const !is IrConst.OfInt) {
            return Transform.Retain(op)
        }

        if (op.const.value != 1) {
            return Transform.Retain(op)
        }

        val new = op.copy(
            const = IrConst.OfInt(value = 2)
        )

        val changes = listOf(
            Change.OfTerm(
                TermChange.Def(op.term(), op = new)
            )
        ) + Change.deriveFrom(new)

        return Transform.Replace(
            new,
            changes
        )
    }
}