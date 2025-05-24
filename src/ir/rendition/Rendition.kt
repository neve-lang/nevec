package ir.rendition

import ir.rendition.names.Names
import ir.rendition.op.OpRender
import ir.structure.Ir
import ir.structure.block.Block
import ir.structure.`fun`.IrFun
import ir.structure.op.Op
import ir.term.TermLike
import util.extension.indent

/**
 * Orchestrates the process of building a human-readable string for [Ir]—think of it like a pretty-printer for the IR.
 */
class Rendition<T : TermLike>(private val ir: Ir<T>) {
    private val blockNames = Names()
    private val termNames = Names()

    /**
     * @return A [String] containing a human-readable version of the IR.
     */
    fun new(): String {
        return renderFunctions(ir.functions)
    }

    private fun renderFunctions(functions: List<IrFun<T>>): String {
        return functions.joinToString("\n", transform = ::renderFunction)
    }

    private fun renderFunction(function: IrFun<T>): String {
        return "fun ${function.mangledName}\n" +
                renderBlocks(function.blocks) +
                "\nend"
    }

    private fun renderBlocks(blocks: List<Block<T>>): String {
        return blocks.joinToString("\n", transform = ::renderBlock)
    }

    private fun renderBlock(block: Block<T>): String {
        val name = blockNames.findNameFor(block.id, block.desiredName)

        return "$name:\n" + block.ops.map(::renderOp).indent().joinToString("\n")
    }

    private fun renderOp(op: Op<T>): String {
        return OpRender<T>(termNames).render(op)
    }
}