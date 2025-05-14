package err.line

import err.note.Note
import file.span.Loc

/**
 * Simplifies the process of building a [Line] object.
 *
 * @see Line for properties.
 */
class LineBuilder(loc: Loc) {
    companion object {
        /**
         * @return A [LineBuilder] from an existing [Line].
         */
        fun from(line: Line) = line.asBuilder()
    }

    private var notes: List<Note> = emptyList()
    private var number: UInt = loc.line
    private var header: String? = null
    private var previous: String? = null
    private var line: String? = null

    /**
     * Builds the [Line].
     *
     * @throws IllegalArgumentException unless [line] is provided (using [withLine]).
     *
     * @return The built [Line].
     */
    fun build(): Line {
        require(line != null) { "A line must be provided" }

        return Line(notes, number, header, previous, line!!)
    }

    /**
     * Adds [notes] to the existing list of [notes].
     *
     * @return The previous [LineBuilder] with [notes].
     */
    fun add(notes: List<Note>) = apply { this.notes += notes }

    /**
     * Sets the [Line]’s header to [msg].
     *
     * @return The previous [LineBuilder] with the [header] set.
     */
    fun header(msg: String?) = apply { this.header = msg }

    /**
     * Sets the [Line]’s previous line to [line].
     *
     * @return The previous [LineBuilder] with the previous line set.
     */
    fun withPrevious(line: String?) = apply { this.previous = line }

    /**
     * Sets the [Line]’s line to [line].
     *
     * @return The previous [LineBuilder] with the line set.
     */
    fun withLine(line: String) = apply { this.line = line }

    /**
     * Sets the [Line]’s previous line to `null`.
     *
     * @return The previous [LineBuilder] with the previous line set.
     */
    fun withoutPrevious() = apply { withPrevious(null) }
}