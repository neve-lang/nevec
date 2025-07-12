package util.twice

/**
 * Represents a tuple where both elements have the same type.
 */
typealias Twice<A> = Pair<A, A>

/**
 * Coerces a [List] into a [Twice].
 *
 * @return A [Twice] with the two first elements of the list of the list’s length is greater or equal to 2.
 * @throws IllegalArgumentException if the list is smaller than that.
 */
fun <T> List<T>.twice(): Twice<T> {
    if (size < 2) {
        throw IllegalArgumentException("List has too few elements to be turned into a `Twice` (min. 2)")
    }

    return take(2).let { (a, b) -> Twice(a, b) }
}