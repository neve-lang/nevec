package err.note

import err.write.Color

enum class NoteKind(val color: Color) {
    INFO(Color.BLUE),
    ERR(Color.RED),
    FIX(Color.GREEN);
}