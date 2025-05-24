package ir.structure.block

import ir.structure.op.Op
import ir.term.TermLike

/**
 * A [Block] represents a **basic block** in a control flow graph in the IR.
 *
 * At the time of writing, Neve does not support branching and control flow graphs, so blocks do not contain a
 * next block.
 */
data class Block<T : TermLike>(val id: Int, val desiredName: String, val ops: List<Op<T>>) {
    companion object {
        /**
         * @return A new [Block] whose [desiredName] is `"bb"`, for “basic block.”
         */
        fun <T : TermLike> basic(id: Int, ops: List<Op<T>>): Block<T> {
            return Block(
                id,
                desiredName = "bb",
                ops
            )
        }
    }
}