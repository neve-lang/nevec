package err.msg

import err.line.Line
import err.report.Report
import err.write.Color
import err.write.Out
import err.write.Write
import file.span.Loc

/**
 * An error message.
 *
 * It may be an error or a simple warning.
 *
 * @see MsgKind
 */
class Msg(
    private val kind: MsgKind,
    private val filename: String,
    private val msg: String,
    private val loc: Loc,
    private val lines: List<Line>
) {
    companion object {
        fun builder() = MsgBuilder()
    }

    fun emit(out: Out) {
        Write.paintedIn(kind.color).saying(" ").saying(kind.symbol.toString()).saying(" ").then().saying(msg).newline()
            .print(out)

        Write.paintedIn(Color.BLUE).saying(" ╭─ ").offset().then().saying(filename).then(Color.GRAY).saying(":$loc")
            .then().newline().print(out)

        lines.forEach { it.emit(out) }

        Write.paintedIn(Color.BLUE).saying(" ╰─ ").offset().then().print(out)
    }

    fun print() = emit(Report.OUT)
}