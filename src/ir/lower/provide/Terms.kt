package ir.lower.provide

import ir.term.life.Moment
import ir.term.warm.Term
import type.Type

/**
 * Takes care of **[Term][ir.warm.term.Term] management** by providing methods that allow creating new terms easily.
 */
class Terms {
    private var nextId = 0

    /**
     * @return A new temporary [Term] with the [Type] and [Moment] given.
     */
    fun newTemporary(type: Type): Term {
        return nextId().let {
            Term.temporary(it, type)
        }
    }

    private fun nextId(): Int {
        return nextId++
    }
}