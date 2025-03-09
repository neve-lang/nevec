package err.msg

import err.write.Color

/**
 * The kind of error message.
 */
enum class MsgKind(val color: Color, val symbol: Char) {
    ERR(Color.RED, '×'),
    WARN(Color.YELLOW, '!')
}