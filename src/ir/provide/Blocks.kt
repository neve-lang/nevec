package ir.provide

import ir.structure.block.Block
import ir.structure.op.Op
import ir.term.warm.Warm

/**
 * Takes care of **[Block] management** by providing methods that allow creating [Blocks][Block] easily.
 */
class Blocks {
    private var nextId = 0

    /**
     * @return A new [Block] with the list of [Ops][Op] given.
     */
    fun newBasic(ops: List<Op<Warm>>): Block<Warm> {
        return Block.basic(nextId(), ops)
    }

    private fun nextId(): Int {
        return nextId++
    }
}