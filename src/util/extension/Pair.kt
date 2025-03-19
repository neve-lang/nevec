package util.extension

/**
 * Functions like a [map] on a List, but applied to both members of a Pair.
 *
 * # Example
 *
 * ```
 * val (begin, end) = loc.extremes().map(UInt::indexable)
 * ```
 */
fun <A, B> Pair<A, A>.map(to: ((A) -> B)) = Pair(to(this.first), to(this.second))
