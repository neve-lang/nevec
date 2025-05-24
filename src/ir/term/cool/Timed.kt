package ir.term.cool

import ir.term.TermLike
import ir.term.life.Lifetime
import type.Type

/**
 * A [Timed] or **timed term** is a [term][ir.term.TermLike] that has a lifetime associated with it.
 *
 * It is sometimes called a “cool term” because it only appears in the “*cool*” IR, i.e. after all optimizations take
 * place.
 */
data class Timed(val id: Int, val desiredName: String, val type: Type, val lifetime: Lifetime) : TermLike {
    override fun desiredName(): String {
        return desiredName
    }

    override fun id(): Int {
        return id
    }
}