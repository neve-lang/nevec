package err.help

import err.line.Line
import err.note.Note
import err.write.Out

/**
 * Helper object that makes the process of building [Line]s from multiple [Note]s easier.
 */
object Lines {
    /**
     * @return A list of [Line] from [notes].
     *
     * If two [Notes][Note] are on the same line, a unique line with both notes is produced.
     */
    fun of(vararg notes: Note): List<Line> {
        return makeLines(notes.toList())
    }

    /**
     * @return A list with a single [Line] from a single [Note], with an optional [header].
     */
    fun single(note: Note, header: String? = null): List<Line> {
        return note.loc.let {
            listOf(
                Line.builder(it).withLine(it.line()).add(
                    listOf(note)
                ).header(header).build()
            )
        }
    }

    private fun makeLines(notes: List<Note>, produced: List<Line> = emptyList()): List<Line> {
        if (notes.isEmpty()) {
            return produced
        }

        val note = notes.first()

        if (produced.isEmpty()) {
            val first = listOf(oneLine(note))
            return makeLines(notes.drop(1), produced = first)
        }

        val last = produced.last()

        return if (note.line() == last.number) {
            val new = last.asBuilder().add(listOf(note)).build()

            makeLines(notes.drop(1), produced.dropLast(1) + listOf(new))
        } else {
            val new = oneLine(note)

            makeLines(notes.drop(1), produced + listOf(new))
        }
    }

    private fun oneLine(from: Note): Line {
        return from.loc.let {
            Line.builder(it).add(listOf(from)).withLine(it.line()).build()
        }
    }
}