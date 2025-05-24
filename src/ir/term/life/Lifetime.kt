package ir.term.life

/**
 * Represents a [Term][ir.term.TermLike]’s lifetime in IR terms.
 *
 * A term’s **lifetime** is a simple construct composed of two values—a *beginning* [Moment], and another *ending*
 * [Moment].
 *
 * @see Moment
 */
data class Lifetime(val begin: Moment, val end: Moment)