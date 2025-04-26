package util.extension

import java.util.*

/**
 * @return [this] with the first char capitalized.
 */
fun String.capitalize(): String {
    return replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else
            it.toString()
    }
}

/**
 * @return [this] prefixed with [what].
 */
fun String.prefixWith(what: String): String {
    return "$what$this"
}

/**
 * @return [this] suffixed with [what].
 */
fun String.suffixWith(what: String): String {
    return "$this$what"
}

/**
 * @param begin The prefix to be applied.
 * @param end The suffix to be applied.
 *
 * @return [this] prefixed with [begin] and suffixed with [end].
 */
fun String.wrappedIn(begin: String, end: String): String {
    return prefixWith(begin).suffixWith(end)
}

/**
 * @return [this] prefixed and suffixed with `"` characters.
 */
fun String.wrappedInQuotes(): String {
    return wrappedIn("\"", "\"")
}

/**
 * @return [this] prefixed and suffixed with `(` and `)` characters, respectively.
 */
fun String.parenthesized(): String {
    return wrappedIn("(", ")")
}

/**
 * @return Both members of the [Pair] of [String] concatenated, with [what] in between them.
 */
fun Pair<String, String>.infixWith(what: String): String {
    return first + what + second
}

/**
 * @return [this] with each element of the list prefixed with two spaces.
 */
fun List<String>.indent(): List<String> {
    return map { it.prefixWith("  ") }
}

/**
 * Wraps the first and last elements of the list by respectively:
 * - Prefixing [first] with the given [begin] argument
 * - Suffixing [last] with the given [end] argument
 *
 * @return a new list with the two new first and last elements modified as described.
 */
fun List<String>.wrappedIn(begin: String, end: String): List<String> {
    val newFirst = first().prefixWith(begin)
    val newLast = last().suffixWith(end)

    val withoutExtremes = drop(1).take(size - 2)

    return listOf(newFirst) + withoutExtremes + listOf(newLast)
}

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

/**
 * @return whether `this` is equal to an empty string with quotes: i.e. `\"\"`
 */
fun String.isIdentityWithQuotes(): Boolean {
    return length <= 2 && filterNot { it == '"' }.isEmpty()
}