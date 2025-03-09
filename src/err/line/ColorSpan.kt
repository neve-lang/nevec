package err.line

import err.note.Note
import err.write.Color

/**
 * A span of colored characters on a single [Line].
 *
 * @property from The beginning *column* of the ColorSpan.  1-indexed.
 * @property to The ending *column* of the ColorSpan.  1-indexed.
 */
data class ColorSpan(val color: Color, val from: UInt, val to: UInt) {
    companion object {
        fun from(note: Note) = ColorSpan(note.color(), note.loc.col, note.loc.col + note.loc.len)
    }
}