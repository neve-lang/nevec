package opt.canvas

import ir.data.change.Change
import ir.data.`fun`.FunData
import ir.provide.IdSystem
import ir.structure.block.Block
import ir.structure.`fun`.IrFun
import ir.structure.op.Op
import ir.term.warm.Warm
import opt.transform.Transform

/**
 * The **Canvas** represents the space in which each optimization pass build its optimized version of the [IrFun] it
 * was given.
 *
 * A Canvas is always built from a previous [IrFun] (the [model])—optimized or not—and a mutable [IdSystem].
 *
 * It carries a list of [Changes][Change].  These are eagerly applied to the [FunData] stored by the Canvas.  The list
 * of Changes is never emptied.
 *
 * The Canvas is immutable—modifying it always produces a new one.
 *
 * @property model The [IrFun] the Canvas uses
 * @property ids The [IdSystem] the Canvas carries.
 * @property produced The produced [IrFun] after the Canvas transformations.
 */
data class Canvas(
    private val model: IrFun<Warm>,
    private val produced: IrFun<Warm>,
    private val ids: IdSystem,
    private val changes: List<Change<Warm>>
) {
    companion object {
        /**
         * @return A new [Canvas] ready for optimization based on a previous [IrFun], along with the given [IdSystem].
         */
        fun from(model: IrFun<Warm>, ids: IdSystem): Canvas {
            return Canvas(
                model,
                produced = model,
                ids,
                changes = emptyList()
            )
        }
    }

    /**
     * @return The produced [IrFun] after all optimizations took place.
     */
    fun extract(): IrFun<Warm> {
        return produced
    }

    /**
     * Goes over every single IR operation of the Canvas’s [model], and applies the resulting [Transform] of the
     * callback when applied to each IR operation.
     *
     * The result is a new Canvas whose [produced] is the [IrFun] produced by all the [Transforms][Transform].
     */
    fun eachOp(
        callback: (IdSystem, FunData<Warm>, Op<Warm>) -> Transform<Op<Warm>>
    ): Canvas {
        return recursiveEachOp(callback, iteration = 0)
    }

    private fun recursiveEachOp(
        callback: (IdSystem, FunData<Warm>, Op<Warm>) -> Transform<Op<Warm>>,
        iteration: Int = 0
    ): Canvas {
        if (iteration >= model.opCount()) {
            return this
        }

        val (produced, newChanges) = applyCallback(callback, iteration)

        return copy(
            produced = produced,
            changes = changes + newChanges
        ).recursiveEachOp(callback, iteration + 1)
    }

    private fun applyCallback(
        callback: (IdSystem, FunData<Warm>, Op<Warm>) -> Transform<Op<Warm>>,
        iteration: Int
    ): Pair<IrFun<Warm>, List<Change<Warm>>> {
        val nthOp = nthModelOp(n = iteration)
        val transform = callback(ids, produced.funData, nthOp)

        return applyTransform(transform, iteration)
    }

    private fun applyTransform(transform: Transform<Op<Warm>>, iteration: Int): Pair<IrFun<Warm>, List<Change<Warm>>> {
        return when (transform) {
            is Transform.Retain -> produced
            is Transform.Replace -> applyReplace(transform, iteration)
        } to transform.changes()
    }

    private fun applyReplace(transform: Transform.Replace<Op<Warm>>, iteration: Int): IrFun<Warm> {
        val funData = applyChanges(transform.changes(), original = produced.funData)

        val startingIndices = produced
            .blocks
            .map { it.ops.size }
            .runningFold(initial = 0) { a, b -> a + b }

        val blocks = produced.blocks.mapIndexed { index, block ->
            val (start, end) = startingIndices.drop(index).take(2)

            if (iteration !in start..<end)
                block
            else
                replacementCopy(block, newOp = transform.new, at = iteration - start)
        }

        return IrFun(
            produced.mangledName,
            blocks,
            funData
        )
    }

    private fun replacementCopy(previousBlock: Block<Warm>, newOp: Op<Warm>, at: Int): Block<Warm> {
        val opList = previousBlock.ops.let {
            ops -> ops.take(at) + newOp + ops.drop(at + 1)
        }

        return Block(previousBlock.id, previousBlock.desiredName, opList)
    }

    private fun applyChanges(changes: List<Change<Warm>>, original: FunData<Warm>): FunData<Warm> {
        return if (changes.isEmpty())
            original
        else
            changes.first().applyTo(applyChanges(
                changes.drop(1),
                original
            ))
    }

    private fun nthModelOp(n: Int): Op<Warm> {
        return flattenedModel()[n]
    }

    private fun flattenedModel(): List<Op<Warm>> {
        return model.blocks.map { it.ops }.flatten()
    }
}