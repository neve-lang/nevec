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
fun <From, To> Pair<From, From>.map(to: (From) -> To) = Pair(to(first), to(second))

/**
 * Takes a [Pair] with members of type [A] and [B], and passes both members to some function.
 *
 * @param to The function in question.  Takes in two parameters, each of type [A], [B] respectively.
 *
 * @return the result of [to].
 */
fun <A, B, Into> Pair<A, B>.into(to: (A, B) -> Into): Into {
    return to(first, second)
}

/**
 * @return whether both members of [this] applied to [predicate] are true.
 */
fun <A> Pair<A, A>.all(predicate: (A) -> Boolean): Boolean {
    return predicate(first) && predicate(second)
}