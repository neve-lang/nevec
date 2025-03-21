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
fun <From, To> Pair<From, From>.map(to: ((From) -> To)) = Pair(to(first), to(second))