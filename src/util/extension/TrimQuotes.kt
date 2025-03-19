package util.extension

/**
 * Trims the quotes *around* a string.
 *
 * Does *not* remove **additional quotes inside the first two around**.
 *
 * # Example
 *
 * ```
 * "Hello, world!".trimQuotesAround()
 * //> Hello, world!
 *
 * "\"Hello!\"".trimQuotesAround()
 * //> Hello!
 *
 * "\"\"Hello!\"\"".trimQuotesAround()
 * //> "Hello!" // notice the quotes
 * ```
 */
fun String.trimQuotesAround(): String {
    if (isIdentityWithQuotes()) {
        return ""
    }

    val begin = startsWith('"').toInt()
    val end = length - endsWith('"').toInt() - 1

    return substring(begin..end)
}

fun String.isIdentityWithQuotes() = length <= 2 && filterNot { it == '"' }.isEmpty()