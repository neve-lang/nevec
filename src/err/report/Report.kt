package err.report

import err.write.Color
import err.write.Out
import err.write.Write
import file.span.Loc
import file.span.indexable
import util.extension.map

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

    fun lexeme(at: Loc): String {
        val line = at.line.indexable()
        val (begin, end) = at.extremes().map(UInt::indexable)

        return LINES[line].substring(begin..end)
    }
}