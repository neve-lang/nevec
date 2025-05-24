package ir.structure

import ir.structure.block.Block
import ir.structure.`fun`.IrFun
import ir.term.TermLike

/**
 * Represents a Neve module’s IR, in its entirety.
 *
 * An IR module is simply made of a list of functions.
 *
 * This is, however, subject to change in the future—namely, when we implement constants.
 *
 * @see Block
 */
data class Ir<T : TermLike>(val functions: List<IrFun<T>>)
