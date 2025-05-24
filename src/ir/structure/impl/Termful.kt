package ir.structure.impl

import ir.term.TermLike

/**
 * Interface for IR operations that use [TermLike][ir.term.TermLike].
 *
 * Provides:
 * - A [term] method which returns the IR operation’s “principal term”—often the *receiver* term for
 *   [Tacs][ir.structure.tac.Tac]
 * - An [allTerms] method that returns a [List] containing all terms the IR operation uses.
 */
interface Termful<T : TermLike> {
    /**
     * @return The implementor operation’s “principal term.”
     *
     * For IR operations that only use one term, such as [Ret][ir.structure.op.Op.Ret], this is defined to be their
     * operand.
     *
     * For Three-Address-Code IR operations, this is defined to be the *receiver term*—the term being assigned.
     *
     * It should also be, by definition, the first item of the list that [allTerms] returns.
     */
    fun term(): T

    /**
     * @return The list of all terms the IR operation operates on.
     */
    fun allTerms(): List<T>

    /**
     * @return Whether the implementor operation is a term definition.
     *
     * By definition, this excludes non three-address-code operations such as [Ret][ir.structure.op.Op.Ret], as well
     * as set-operations such as [TableSet][ir.structure.tac.Tac.TableSet].
     */
    fun isDefinition(): Boolean
}