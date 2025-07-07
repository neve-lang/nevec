package ir.structure

import ir.provide.IdSystem
import ir.structure.block.Block
import ir.structure.`fun`.IrFun
import ir.term.TermLike

/**
 * Represents a Neve module’s IR, in its entirety.
 *
 * An IR module is simply made of a list of functions.
 *
 * This is, however, subject to change in the future—namely, when we implement user-defined constants.
 *
 * @see Block
 */
data class Ir<T : TermLike>(
    val functions: List<IrFun<T>>,
    val ids: IdSystem
)
