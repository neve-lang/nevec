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

fun <A> Pair<A, A>.all(predicate: (A) -> Boolean) = predicate(first) && predicate(second)

fun <A, B> Pair<A, B>.each(predicate: (A, B) -> Boolean) = predicate(first, second)