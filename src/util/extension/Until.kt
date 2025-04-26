package util.extension

/**
 * Repeats [what] as long as [predicate] is false.
 *
 * @param predicate The predicate determining termination.
 * @param what The action to be repeated for as long as [predicate] is `false`.
 *
 * @return a list of results produced by [what].
 */
fun <T> until(predicate: () -> Boolean, what: () -> T): List<T> {
    val list = mutableListOf<T>()

    while (!predicate()) {
        list.add(what())
    }

    return list.toList()
}