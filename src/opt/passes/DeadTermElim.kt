package opt.passes

import ir.data.change.Change
import ir.data.change.TermChange
import ir.structure.op.Op
import ir.term.warm.Warm
import opt.canvas.Canvas
import opt.structure.id.OptId
import opt.structure.pass.Pass
import opt.transform.Transform

/**
 * The dead term elimination pass.
 *
 * This optimization pass removes the definitions of [Terms][ir.term.TermLike] that aren't used by any IR operation.
 */
class DeadTermElim : Pass {
    override fun id(): OptId {
        return OptId.DEAD_TERM_ELIM
    }

    override fun apply(to: Canvas): Canvas {
        return to.eachOp { _, data, op ->
            if (op.isDefinition() && data.usesOf(op.term()).isEmpty())
                removeTerm(op)
            else
                Transform.Retain(op)
        }
    }

    private fun removeTerm(op: Op<Warm>): Transform.Remove<Op<Warm>> {
        val changes = listOf(
            TermChange.Def(op.term(), null).wrap(),
        ) + unconstChange(op)

        return Transform.Remove(changes)
    }

    private fun unconstChange(op: Op<Warm>): List<Change<Warm>> {
        return if (op is Op.Const)
            listOf(Change.Unconst(op.const, op.term()))
        else
            emptyList()
    }
}