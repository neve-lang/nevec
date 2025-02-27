package err.msg

import err.line.Line
import info.span.Loc

class Msg(kind: MsgKind, filename: String, msg: String, loc: Loc, lines: List<Line>) {
    companion object {
        fun builder() = MsgBuilder()
    }
}