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