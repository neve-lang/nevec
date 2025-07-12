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
fun <From, To> Pair<From, From>.map(to: (From) -> To): Pair<To, To> {
    return Pair(to(first), to(second))
}

/**
 * Takes a [Pair] with members of type [A] and [B], and passes both members to some function.
 *
 * @param to The function in question.  Takes in two parameters, each of type [A], [B] respectively.
 *
 * @return The result of [to].
 */
fun <A, B, Into> Pair<A, B>.into(to: (A, B) -> Into): Into {
    return to(first, second)
}

/**
 * @return Whether either member of [this] applied to [predicate] is true.
 */
fun <A> Pair<A, A>.any(predicate: (A) -> Boolean): Boolean {
    return predicate(first) || predicate(second)
}

/**
 * @return Whether no member of [this] applied to [predicate] is true.
 */
fun <A> Pair<A, A>.none(predicate: (A) -> Boolean): Boolean {
    return !any(predicate)
}

/**
 * @return Whether both members of [this] applied to [predicate] are true.
 */
fun <A> Pair<A, A>.all(predicate: (A) -> Boolean): Boolean {
    return predicate(first) && predicate(second)
}

/**
 * @return A copy of this [Pair] where:
 *
 * - The first element is the second,
 * - The second element is the first.
 */
fun <A, B> Pair<A, B>.flip(): Pair<B, A> {
    return second to first
}

/**
 * @return An [Array] of two elements, containing both members of [this].
 */
inline fun <reified A> Pair<A, A>.unpacked(): Array<A> {
    return arrayOf(first, second)
}