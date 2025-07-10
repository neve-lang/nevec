package opt.passes

import ir.data.change.Change
import ir.data.change.TermChange
import ir.data.`fun`.FunData
import ir.structure.consts.IrConst
import ir.structure.op.Op
import ir.term.warm.Warm
import opt.canvas.Canvas
import opt.help.TermReplacer
import opt.structure.id.OptId
import opt.structure.pass.Pass
import opt.transform.Transform

/**
 * The “constant reuse” optimization pass.
 *
 * This optimization pass tries to eliminate redundant re-definitions of a same constant.
 *
 * For example, it rewrites the following code:
 *
 * ```
 * t0 = 1
 * t1 = 1
 * t2 = t0 + t1
 * ```
 *
 * Into:
 *
 * ```
 * t0 = 1
 * t1 = t0 + t0
 * ```
 */
class ConstReuse : Pass {
    override fun id(): OptId {
        return OptId.CONST_REUSE
    }

    override fun apply(to: Canvas): Canvas {
        return to.eachOp { _, data, op ->
            when (op) {
                is Op.Const -> removeDefIfNeeded(op, data)
                else -> changeTermsIfPossible(op, data)
            }
        }
    }

    private fun removeDefIfNeeded(op: Op.Const<Warm>, data: FunData<Warm>): Transform<Op<Warm>> {
        return if (isFirstDef(op.term(), op.const, data))
            Transform.Retain(op)
        else
            Transform.Remove(removeDefChanges(op))
    }

    private fun changeTermsIfPossible(op: Op<Warm>, data: FunData<Warm>): Transform<Op<Warm>> {
        val constOperands = op.allTerms().drop(1).filter {
            data.defOf(it) is Op.Const
        }

        if (constOperands.isEmpty()) {
            return Transform.Retain(op)
        }

        return changeTerms(op, data, constOperands)
    }

    private fun changeTerms(op: Op<Warm>, data: FunData<Warm>, constOperands: List<Warm>): Transform<Op<Warm>> {
        val constDefs = constOperands.map { data.defOf(it) }.filter {
            (it as Op.Const).let { op ->
                !isFirstDef(op.term(), op.const, data)
            }
        }

        val replacingTerms = constDefs.map { firstTermOf((it as Op.Const).const, data) }

        if (constDefs.isEmpty() || replacingTerms.isEmpty()) {
            return Transform.Retain(op)
        }

        val (oldTerms, newTerms) = constDefs.mapNotNull { it?.term() } to replacingTerms.filterNotNull()

        val newOp = TermReplacer.replace(op, oldTerms, newTerms)
        val changes = TermChange.Unuse(oldTerms, op)
            .wrap()
            .let { listOf(it) }

        return Transform.Replace(
            new = newOp,
            changes = changes
        )
    }

    private fun firstTermOf(const: IrConst, data: FunData<Warm>): Warm? {
        return data.termsUsing(const).lastOrNull()
    }

    private fun isFirstDef(term: Warm, const: IrConst, data: FunData<Warm>): Boolean {
        return term == firstTermOf(const, data)
    }

    private fun removeDefChanges(op: Op.Const<Warm>): List<Change<Warm>> {
        return listOf(
            Change.Unconst(op.const, op.term())
        )
    }
}