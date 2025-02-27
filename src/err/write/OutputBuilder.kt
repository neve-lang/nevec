package err.write

class OutputBuilder {
    private val strings = mutableListOf<String>()

    private var withOffset = false

    fun offset() = apply { withOffset = true }

    fun saying(msg: String) = apply { strings.add(msg) }
    fun and(msg: String) = apply { saying(msg) }
    fun paintedIn(color: Color) = apply { saying(color.code) }
    fun newline() = apply { saying("\n") }
    fun then() = apply { paintedIn(Color.NONE) }
    fun then(color: Color) = apply { then().paintedIn(color) }

    fun print(out: Out) {
        val string = strings.joinToString("")

        if (withOffset) {
            out.offset()
        }

        out.write(string)
    }
}