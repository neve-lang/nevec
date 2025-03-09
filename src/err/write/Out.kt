package err.write

import kotlin.math.abs
import kotlin.math.log10

/**
 * Abstraction layer around the process of writing to STDERR and managing [offset].
 *
 * @param maxLine The number of lines in the source file.
 */
class Out(maxLine: Int) {
    private val offset = maxLine.digits()

    companion object {
        /**
         * Errors that occur before reading the source file.
         */
        fun fatal() = Out(0)
    }

    fun write(what: CharSequence) {
        System.err.print(what)
    }

    fun end() {
        System.err.println()
    }

    fun color(color: Color) {
        write(color.code)
    }

    fun reset() {
        color(Color.NONE)
    }

    fun newline() {
        write("\n")
    }

    fun offset() {
        write(" ".repeat(offset))
    }
}

fun Int.digits() = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}