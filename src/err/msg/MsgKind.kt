package err.msg

import err.write.Color

enum class MsgKind(color: Color, symbol: Char) {
    ERR(Color.RED, '×'),
    WARN(Color.YELLOW, '!')
}