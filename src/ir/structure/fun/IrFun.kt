package ir.structure.`fun`

import ir.structure.block.Block
import ir.data.`fun`.FunData
import ir.term.TermLike

/**
 * Represents an IR function.
 *
 * An IR function is made of a list of [basic blocks][Block], linked together in a **control flow graph**.
 *
 * At the time of writing, Neve does not support branching—therefore, [blocks] will always be singleton.
 */
data class IrFun<T : TermLike>(
    val mangledName: String,
    val blocks: List<Block<T>>,
    val funData: FunData<T>
) {
    companion object {
        /**
         * @return A new [IrFun] from a list of basic blocks, with a [name] provided.
         */
        fun <T : TermLike> from(blocks: List<Block<T>>, name: String): IrFun<T> {
            return IrFun(
                name,
                blocks,
                FunData.from(blocks)
            )
        }
    }
}