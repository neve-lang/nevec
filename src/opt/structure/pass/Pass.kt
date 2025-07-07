package opt.structure.pass

import ir.structure.`fun`.IrFun
import opt.canvas.Canvas
import opt.structure.id.OptId

/**
 * Represents an optimization pass.
 *
 * It provides the following methods:
 *
 * - An [id] method, which returns the [OptId] to be associated with this pass.
 * - An [apply] method, which applies the optimization pass to an [IrFun], and returns a new [IrFun].
 */
interface Pass {
    /**
     * @return The implementor [Pass]’s [OptId].
     */
    fun id(): OptId

    /**
     * @return A new [Canvas] with the optimization pass applied.
     */
    fun apply(to: Canvas): Canvas
}