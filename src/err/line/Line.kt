package err.line

import err.note.Note
import err.write.Color
import err.write.Out
import err.write.Write
import info.span.Loc

class Line(
    private val notes: List<Note>,
    val number: UInt,
    private val header: String?,
    private val previous: String?,
    private val line: String
) {
    companion object {
        fun builder(loc: Loc) = LineBuilder(loc)
    }

    fun asBuilder(): LineBuilder {
        return LineBuilder(Loc.onLine(number)).add(notes).header(header).withLine(line).withPrevious(previous)
    }

    fun emit(out: Out) {
        val notes = notes.sortedBy { it.loc.col }
        val colorSpans = ColorSpans(notes.map(ColorSpan::from), line)

        writeHeader(out)

        display(colorSpans, out)
        writeNotes(notes, out)

        out.reset()
    }

    private fun writeHeader(out: Out) {
        if (header != null) {
            Write.paintedIn(Color.BLUE).saying(" ├─ ").then().and(header).offset().newline().print(out)
        }
    }

    private fun display(colorSpans: ColorSpans, out: Out) {
        if (previous != null) {
            displayPrevious(out)
        }

        locus(out = out)

        colorSpans.color(out)
        out.newline()
    }

    private fun writeNotes(notes: List<Note>, out: Out) {
        underlines(notes, out)
        hangs(notes, out)
    }

    private fun displayPrevious(out: Out) {
        if (number == 1u) {
            return
        }

        val previousNumber = number - 1u
        locus(number = previousNumber, out)

        Write.paintedIn(Color.GRAY).saying(previous!!).then().newline().print(out)
    }

    private fun underlines(notes: List<Note>, out: Out) {
        emptyLocus(out)

        notes.zip(listOf(1u) + notes.map(Note::until)).forEach { (n, col) ->
            n.underline(col, out)
        }

        out.newline()
    }

    private fun hangs(notes: List<Note>, out: Out) {
        val mutNotes = notes.toMutableList()

        while (mutNotes.isNotEmpty()) {
            emptyLocus(out)
            eachHang(mutNotes, out)

            out.newline()

            mutNotes.removeLast()
        }
    }

    private fun eachHang(notes: List<Note>, out: Out) {
        var window = notes
        val last = notes.lastOrNull() ?: return

        (1u..last.end).forEach { col ->
            val head = window.firstOrNull() ?: return

            if (col != head.hang) {
                out.write(" ")
            } else {
                when (head) {
                    last -> Write.paintedIn(head.color()).saying("╰─ ").and(head.msg).then().print(out)
                    else -> Write.paintedIn(head.color()).saying("│").then().print(out)
                }

                window = window.drop(1)
            }

        }
    }

    private fun locus(number: UInt = this.number, out: Out) {
        Write.paintedIn(Color.GRAY).saying(number.toString()).then(Color.BLUE).saying(" │ ").then().print(out)
    }

    private fun emptyLocus(out: Out) {
        Write.paintedIn(Color.BLUE).saying(" · ").offset().print(out)
    }
}