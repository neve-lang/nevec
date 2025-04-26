package util.extension

/**
 * @return the first key in [this] that maps to [from], `null` otherwise.
 */
fun <K, V> Map<K, V>.key(from: V): K? {
    return filterValues { it == from }.keys.firstOrNull()
}

