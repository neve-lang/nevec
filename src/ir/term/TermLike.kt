package ir.term

import ir.term.id.TermId

/**
 * Marker interface that represents a **term-like component of the IR**—this includes [Terms][ir.warm.term.Term] and
 * [Timeds][ir.cool.timed.Timed].
 *
 * It is useful, because it allows us to describe two kinds of IR:
 *
 * - A first IR which we call “*warm*,” where all optimizations take place
 * - And a second IR which we call “*cool*,” where no more optimizations happen and the lifetime of each term—which are
 *   now referred to as “*timeds*”—is recorded.
 *
 * @see ir.warm.term.Term
 */
interface TermLike {
    /**
     * @return The implementor term’s desired name.
     *
     * @see ir.warm.term.Term.desiredName
     */
    fun desiredName(): String

    /**
     * @return The implementor term’s ID.
     *
     * @see ir.warm.term.Term.id
     */
    fun id(): TermId
}