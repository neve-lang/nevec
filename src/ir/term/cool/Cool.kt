package ir.term.cool

/**
 * Simple type alias to make code clearer.  This allows us to distinguish between the “warm” and “cool” IR in
 * a more readable way:
 *
 * ```
 * Add<Cool> vs. Add<Timed>
 * ```
 */
typealias Cool = Timed
