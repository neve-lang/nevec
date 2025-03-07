package err.line

import err.note.Note
import file.span.Loc

class LineBuilder(loc: Loc) {
    companion object {
        fun from(line: Line) = line.asBuilder()
    }

    private var notes: List<Note>? = null
    private var number: UInt = loc.line
    private var header: String? = null
    private var previous: String? = null
    private var line: String? = null

    fun build(): Line {
        require(line != null) { "A line must be provided" }

        return Line(notes.orEmpty(), number, header, previous, line!!)
    }

    fun add(notes: List<Note>) = apply { this.notes = notes }

    fun header(msg: String?) = apply { this.header = msg }

    fun withPrevious(line: String?) = apply { this.previous = line }

    fun withLine(line: String) = apply { this.line = line }

    fun withoutPrevious() = apply { withPrevious(null) }
}