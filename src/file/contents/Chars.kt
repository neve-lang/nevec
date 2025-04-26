package file.contents

/**
 * Used alongside [lex.Lex]; stores the chars in the source string as an iterator.
 */
class Chars(private var string: String) {
    private var at: Int = 0

    /**
     * @return the next character **without advancing** if there is one, `null` otherwise.
     */
    fun peek(): Char? {
        return string.getOrNull(at)
    }

    /**
     * @return a [String] of length [n] if there are [n] or more characters remaining in the original string,
     * `null` otherwise.
     */
    fun peek(n: Int): String? {
        return if (n <= remaining())
            string.substring(at, at + n)
        else
            null
    }

    /**
     * @return the next character **and advances to the next**, `null` otherwise.
     */
    fun next(): Char? {
        return peek()?.also { at++ }
    }

    private fun remaining(): Int {
        return string.length - at
    }
}