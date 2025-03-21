package util.extension

import java.util.Locale

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

fun List<String>.wrappedIn(begin: String, end: String) = listOf(begin) + this + listOf(end)

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
