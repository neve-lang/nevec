package util.extension

import java.util.*

fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase())
        it.titlecase(Locale.getDefault())
    else
        it.toString()
}

fun String.prefixWith(what: String) = "$what$this"

fun String.suffixWith(what: String) = "$this$what"

fun String.wrappedIn(begin: String, end: String) = prefixWith(begin).suffixWith(end)

fun String.wrappedInQuotes() = wrappedIn("\"", "\"")

fun String.parenthesized() = wrappedIn("(", ")")

fun Pair<String, String>.infixWith(what: String) = first + what + second

fun List<String>.indent() = map { "  $it" }

/**
 * Wraps the first and last elements of the list by respectively:
 * * Prefixing [first] with the given [begin] argument
 * * Suffixing [last] with the given [end] argument
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

fun String.isIdentityWithQuotes() = length <= 2 && filterNot { it == '"' }.isEmpty()
