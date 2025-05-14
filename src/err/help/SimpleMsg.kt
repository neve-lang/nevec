package err.help

import err.line.Line
import err.msg.Msg
import err.note.Note
import err.report.Report
import file.span.Loc

/**
 * Helper object that makes building simple error messages easier.
 */
object SimpleMsg {
    /**
     * @return A [Msg] whose only line is that at [loc], error message is that given by [msg], and only note is
     * that at [loc] and whose message is [saying].  A [header] may also be given, but it is optional.
     */
    fun at(loc: Loc, msg: String, saying: String, header: String? = null): Msg {
        return Report.err(loc, msg).lines(
            listOf(
                Line.builder(loc)
                    .header(header)
                    .withLine(loc.line())
                    .add(
                        listOf(Note.err(loc, saying))
                    ).build()
            )
        ).build()
    }
}