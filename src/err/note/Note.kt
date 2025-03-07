package err.note

import err.write.Out
import file.span.Loc

class Note(private val kind: NoteKind, val loc: Loc, val msg: String) {
    private val len = loc.col + loc.len - 1u
    private val begin = loc.col

    val hang = loc.col + loc.len / 2u
    val end = begin + len

    companion object {
        fun info(loc: Loc, msg: String) = Note(NoteKind.INFO, loc, msg)

        fun err(loc: Loc, msg: String) = Note(NoteKind.ERR, loc, msg)

        fun fix(loc: Loc, msg: String) = Note(NoteKind.FIX, loc, msg)
    }

    fun underline(from: UInt, out: Out) {
        out.color(color())
        (from..len).forEach { out.write(next(it)) }
        out.reset()
    }

    fun color() = kind.color
    fun until() = len + 1u

    private fun next(col: UInt): String {
        return when (col) {
            hang -> "┬"
            in begin..len -> "─"
            else -> " "
        }
    }
}