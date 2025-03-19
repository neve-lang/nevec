package util.extension

fun <T> until(predicate: () -> Boolean, what: () -> T): List<T> {
    val list = mutableListOf<T>()

    while (!predicate()) {
        list.add(what())
    }

    return list.toList()
}