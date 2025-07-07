package opt

import ctx.Ctx
import ir.provide.IdSystem
import ir.structure.Ir
import ir.structure.`fun`.IrFun
import ir.term.warm.Warm
import nevec.result.Aftermath
import opt.canvas.Canvas
import opt.passes.SillyPass
import opt.structure.pass.Pass
import stage.Stage

/**
 * The optimization stage.
 *
 * It is responsible for applying optimization passes to a module’s IR.
 *
 * This stage repeatedly applies all optimization passes to all functions until either:
 *
 * - The repetition threshold is reached, which is 3 on debug builds and 10 on release builds
 * - A repetition does not cause any modifications throughout the module.
 */
class Opt : Stage<Ir<Warm>, Ir<Warm>> {
    companion object {
        private val PASSES = listOf<Pass>(
            SillyPass()
        )
    }

    override fun perform(data: Ir<Warm>, ctx: Ctx): Aftermath<Ir<Warm>> {
        return Ir(
            data.functions.map { optimize(it, ctx, data.ids) },
            data.ids
        ).let {
            Aftermath.Success(it)
        }
    }

    private fun optimize(irFun: IrFun<Warm>, ctx: Ctx, ids: IdSystem): IrFun<Warm> {
        return repeatedOptimization(
            Canvas.from(irFun, ids),
            ctx,
            repetition = 0
        ).extract()
    }

    private fun repeatedOptimization(canvas: Canvas, ctx: Ctx, repetition: Int): Canvas {
        if (repetition == repetitionThreshold(ctx)) {
            return canvas
        }

        val optimized = PASSES.fold(initial = canvas) { acc, pass -> pass.apply(to = acc) }
        return repeatedOptimization(
            optimized,
            ctx,
            repetition + 1
        )
    }

    private fun repetitionThreshold(ctx: Ctx): Int {
        // for now, return 3 by default.
        return 3
    }
}