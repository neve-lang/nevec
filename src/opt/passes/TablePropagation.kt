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
import util.extension.map
import util.twice.Twice

/**
 * The table propagation optimization pass.
 *
 * This optimization pass is responsible for condensing table initializations into one single constant that can be
 * loaded at startup time—for example, consider this program:
 *
 * ```
 * t0 = "type"
 * t1 = "quality"
 * t2 = "Banana"
 * t3 = "Excellent"
 * fruit0 = [:]
 * fruit0[t0] = t2
 * fruit0[t1] = t3
 * ```
 *
 * Whenever possible, the [TablePropagation] optimization pass condenses this code into this
 * (after dead term elimination):
 *
 * ```
 * fruit0 = ["type": "Banana", "quality": "Excellent"]
 * ```
 */
class TablePropagation : Pass {
    override fun id(): OptId {
        return OptId.TABLE_PROPAGATION
    }

    override fun apply(to: Canvas): Canvas {
        return to.eachOp { _, data, op ->
            when (op) {
                is Op.Const -> tablePropagateIfPossible(op, data)
                is Op.OfTac -> removeTableSetIfPossible(op, data)
                else -> Transform.Retain(op)
            }
        }
    }

    private fun tablePropagateIfPossible(op: Op.Const<Warm>, data: FunData<Warm>): Transform<Op<Warm>> {
        return when (op.const) {
            is IrConst.OfTable, IrConst.OfEmptyTable -> tablePropagate(op, data)
            else -> Transform.Retain(op)
        }
    }

    private fun removeTableSetIfPossible(op: Op.OfTac<Warm>, data: FunData<Warm>): Transform<Op<Warm>> {
        return when (val tac = op.tac) {
            is Tac.TableSet -> removeTableSet(tac, data)
            else -> Transform.Retain(op)
        }
    }

    private fun tablePropagate(op: Op.Const<Warm>, data: FunData<Warm>): Transform<Op<Warm>> {
        val (oldKeys, oldVals) = oldKeysAndVals(op)

        val (keys, vals) = keysAndVals(op, data)
        val (newKeys, newVals) = (oldKeys + keys) to (oldVals + vals)

        if (newKeys == oldKeys && newVals == oldVals) {
            return Transform.Retain(op)
        }

        val newConst = if (newKeys.isNotEmpty())
            IrConst.OfTable(newKeys, newVals)
        else
            IrConst.OfEmptyTable

        val newOp = Op.Const(op.term(), newConst, op.info)

        return Transform.Replace(
            new = newOp,
            changes = Change.deriveFrom(newOp)
        )
    }

    private fun removeTableSet(tac: Tac.TableSet<Warm>, data: FunData<Warm>): Transform<Op<Warm>> {
        val table = data.defOf(tac.term())
        val (keyDef, valueDef) = (tac.key to tac.value).map { data.defOf(it) }

        if (
            table == null ||
            keyDef == null ||
            valueDef == null ||
            !areConsts(keyDef, valueDef)
        ) {
            return Transform.Retain(tac.wrap())
        }

        val (key, value) = (keyDef to valueDef).map { (it as Op.Const).const }

        if (!tableHasEntry(table, key, value)) {
            return Transform.Retain(tac.wrap())
        }

        val changes = TermChange.Unuse(tac.allTerms(), tac.wrap())
            .wrap()
            .let { listOf(it) }

        return Transform.Remove(changes)
    }

    private fun keysAndVals(op: Op.Const<Warm>, data: FunData<Warm>): Twice<List<IrConst>> {
        val tableSets = data
            .usesOf(op.term())
            // reverse to make sure we get the right order
            .reversed()
            .filterIsInstance<Op.OfTac<Warm>>()
            .map { it.tac }
            .filterIsInstance<Tac.TableSet<Warm>>()

        val keyDefs = tableSets.map { it.key }.mapNotNull { data.defOf(it) }
        val valDefs = tableSets.map { it.value }.mapNotNull { data.defOf(it) }

        val (keys, vals) = (keyDefs to valDefs).map {
            it
                .filterIsInstance<Op.Const<Warm>>()
                .map { op -> op.const }
        }

        return (keys zip vals).unzip()
    }

    private fun oldKeysAndVals(op: Op.Const<Warm>): Twice<List<IrConst>> {
        val oldTable = when (val table = op.const) {
            is IrConst.OfTable -> table
            is IrConst.OfEmptyTable -> IrConst.OfTable(emptyList(), emptyList())

            else -> throw IllegalArgumentException(
                "Unexpected non-table `IrConst` in `oldKeysAndVals`."
            )
        }

        return oldTable.keys to oldTable.vals
    }

    private fun areConsts(vararg ops: Op<Warm>): Boolean {
        return ops.all { it is Op.Const }
    }

    private fun tableHasEntry(tableDef: Op<Warm>, key: IrConst, value: IrConst): Boolean {
        if (tableDef !is Op.Const || tableDef.const !is IrConst.OfTable) {
            return false
        }

        val table = tableDef.const

        return (table.keys zip table.vals).any {
            it == (key to value)
        }
    }
}