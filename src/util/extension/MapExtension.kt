package util.extension

/**
 * @return the first key in [this] that contains [from].
 */
fun <K, V> Map<K, V>.key(from: V) = filterValues { it == from }.keys.firstOrNull()

