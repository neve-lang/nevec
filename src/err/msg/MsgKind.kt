package err.msg

import err.write.Color

enum class MsgKind(val color: Color, val symbol: Char) {
    ERR(Color.RED, '×'),
    WARN(Color.YELLOW, '!')
}