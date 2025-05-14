package err.note

import err.write.Color
import err.write.Out
import file.span.Loc

/**
 * A note that goes along a [err.line.Line].
 *
 * A [err.line.Line] may have multiple.
 */
class Note(private val kind: NoteKind, val loc: Loc, val msg: String) {
    private val len = loc.col + loc.len - 1u
    private val begin = loc.col

    val hang = loc.col + loc.len / 2u
    val end = begin + len

    companion object {
        /**
         * @return A [Note] whose [NoteKind] is [NoteKind.INFO].
         */
        fun info(loc: Loc, msg: String): Note {
            return Note(NoteKind.INFO, loc, msg)
        }

        /**
         * @return A [Note] whose [NoteKind] is [NoteKind.ERR].
         */
        fun err(loc: Loc, msg: String): Note {
            return Note(NoteKind.ERR, loc, msg)
        }

        /**
         * @return A [Note] whose [NoteKind] is [NoteKind.FIX].
         */
        fun fix(loc: Loc, msg: String): Note {
            return Note(NoteKind.FIX, loc, msg)
        }
    }

    /**
     * Writes the underline for `this` [Note].
     */
    fun underline(from: UInt, out: Out) {
        out.color(color())
        (from..len).forEach { out.write(next(it)) }
        out.reset()
    }

    /**
     * @return This [Note]’s appropriate color.
     */
    fun color(): Color {
        return kind.color
    }

    /**
     * @return This [Note]’s line.
     */
    fun line(): UInt {
        return loc.line
    }

    /**
     * @return This [Note]’s [len] plus one.
     */
    fun until(): UInt {
        return len + 1u
    }

    private fun next(col: UInt): String {
        return when (col) {
            hang -> "┬"
            in begin..len -> "─"
            else -> " "
        }
    }
}