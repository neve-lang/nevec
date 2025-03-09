package err.line

import err.write.Out

/**
 * Responsible for coloring the characters on a line based on a List of [ColorSpan].
 */
class ColorSpans(private val spans: List<ColorSpan>, private val chars: CharSequence) {
    /**
     * Colors the line based on the given [chars].
     * The line is immediately output to STDERR.
     */
    fun color(out: Out) {
        val froms = spans.map { it.from }
        val tos = spans.map { it.to }

        colorChars(froms, tos, out)
    }

    private fun colorChars(froms: List<UInt>, tos: List<UInt>, out: Out) {
        chars.forEachIndexed { col, c ->
            val index = froms.indexOfFirst { it.on(col) }
            if (index != -1) {
                val span = spans[index]
                out.color(span.color)
            }

            if (tos.any { it.on(col) }) {
                out.reset()
            }

            out.write(c.toString())
        }
    }
}

fun UInt.on(int: Int) = this == int.toUInt() + 1u