package ir.term.warm

import ir.term.TermLike
import ir.term.id.TermId
import type.Type

/**
 * A **term** in the Neve compiler represents an IR “symbol”—i.e., something that holds a value.
 *
 * An example term may be:
 *
 * ```
 * t0 = 1
 * ```
 *
 * However, [Terms][Term] may not be confused with [Timeds][ir.cool.timed.Timed], as terms do not carry lifetimes,
 * whereas timeds do.
 *
 * @property desiredName The [Term]’s desired name—it may not reflect the name in the IR output.  For temporary terms,
 * it’s common for the [desiredName] to be `"t"`.
 *
 * @see TermLike
 */
data class Term(val id: TermId, val desiredName: String, val type: Type) : TermLike {
    companion object {
        /**
         * @return A new [Term] with the given [id] and [type], whose [desiredName] is `"t"`.
         */
        fun temporary(id: Int, type: Type): Term {
            return Term(
                id,
                desiredName = "t",
                type,
            )
        }
    }

    override fun desiredName(): String {
        return desiredName
    }

    override fun id(): Int {
        return id
    }
}