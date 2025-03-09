package err.report

import err.msg.Msg
import err.msg.MsgBuilder
import err.write.Color
import err.write.Out
import err.write.Write
import file.span.Loc
import file.span.indexable
import util.extension.map

/**
 * Simplifies the process of building error [Msg]s by storing the file name and the source lines of the current
 * [file.module.Module].
 */
object Report {
    lateinit var FILENAME: String
    private lateinit var LINES: List<String>

    fun setup(filename: String, lines: List<String>) {
        FILENAME = filename
        LINES = lines
    }

    fun fileErr(filename: String) {
        Write.paintedIn(Color.RED).saying(" × ").then().saying("could not read '$filename'").print(Out.fatal())
    }

    fun err(loc: Loc, msg: String) = Msg.builder().msg(msg).loc(loc)

    fun lexeme(at: Loc): String {
        val (begin, end) = at.extremes().map(UInt::indexable)

        return at.line().substring(begin..end)
    }

    fun line(at: Loc) = LINES[at.line.indexable()]
}