package err.note

import err.write.Color

/**
 * The kind of note we want to display.
 *
 * It denotes the note's color.
 */
enum class NoteKind(val color: Color) {
    INFO(Color.BLUE),
    ERR(Color.RED),
    FIX(Color.GREEN);
}