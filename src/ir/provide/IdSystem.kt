package ir.provide

import ir.structure.block.Block
import ir.structure.op.Op
import ir.term.warm.Term
import ir.term.warm.Warm
import type.Type

/**
 * Groups together the [Terms] and [Blocks] providers into a single class.
 *
 * It always provides **warm terms**.
 *
 * @see ir.term.TermLike
 */
class IdSystem {
    private val terms = Terms()
    private val blocks = Blocks()

    /**
     * @return A new temporary term with a unique ID.
     */
    fun newTerm(type: Type): Term {
        return terms.newTemporary(type)
    }

    /**
     * @return A new basic block with a unique ID.
     */
    fun newBlock(ops: List<Op<Warm>>): Block<Warm> {
        return blocks.newBasic(ops)
    }
}