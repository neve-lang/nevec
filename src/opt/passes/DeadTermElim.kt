package opt.passes

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
                removeTerm(op.term())
            else
                Transform.Retain(op)
        }
    }

    private fun removeTerm(term: Warm): Transform.Remove<Op<Warm>> {
        val changes = TermChange.Def(term, null)
            .wrap()
            .let { listOf(it) }

        return Transform.Remove(changes)
    }
}