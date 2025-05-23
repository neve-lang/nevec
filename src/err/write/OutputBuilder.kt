package err.write

/**
 * Simplifies the process of building a stylized String for STDERR.
 */
class OutputBuilder {
    private val strings = mutableListOf<String>()

    private var withOffset = false

    fun print(out: Out) {
        val string = strings.joinToString("")

        if (withOffset) {
            out.offset()
        }

        out.write(string)
    }

    fun offset() = apply { withOffset = true }

    fun saying(msg: String) = apply { strings.add(msg) }

    fun and(msg: String) = apply { saying(msg) }

    fun paintedIn(color: Color) = apply { saying(color.code) }

    fun newline() = apply { saying("\n") }

    fun then() = apply { paintedIn(Color.NONE) }

    fun then(color: Color) = apply { then().paintedIn(color) }

}