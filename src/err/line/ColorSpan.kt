package err.line

import err.note.Note
import err.write.Color

data class ColorSpan(val color: Color, val from: UInt, val to: UInt) {
    companion object {
        fun from(note: Note) = ColorSpan(note.color(), note.loc.col, note.loc.col + note.loc.len)
    }
}