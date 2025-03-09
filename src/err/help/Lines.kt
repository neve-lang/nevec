package err.help

import err.line.Line
import err.note.Note

/**
 * Helper object: makes the process of building [Line]s from multiple [Note]s easier.
 */
object Lines {
    fun of(vararg notes: Note) = notes.map {
        val loc = it.loc
        Line.builder(loc).add(listOf(it)).withLine(loc.line()).build()
    }
}