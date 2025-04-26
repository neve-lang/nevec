package util.extension

/**
 * @return `1` if [this] is `true`, `0` otherwise.
 */
fun Boolean.toInt(): Int {
    return if (this)
        1
    else
        0
}
