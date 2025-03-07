package file.contents

class Chars(private var string: String) {
    private var at: Int = 0

    fun peek() = string.getOrNull(at)

    fun peek(n: Int) = if (n <= remaining()) string.substring(at, at + n)
    else null

    fun next() = peek().also { at++ }

    private fun remaining() = string.length - at
}