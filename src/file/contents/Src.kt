package file.contents

import err.report.Report
import err.write.Out
import file.span.Loc
import file.span.indexable
import util.extension.map
import java.io.File
import java.io.IOException

/**
 * Abstraction layer around setting up [Report] and reading a file.
 */
object Src {
    lateinit var FILENAME: String
    private lateinit var LINES: List<String>

    /**
     * Reads the file named [filename].
     *
     * @return A pair of String and List<String>, which respectively correspond to the source string and the source
     * lines.
     */
    fun read(filename: String): Pair<String, List<String>> {
        val lines = try {
            File(filename).bufferedReader().readLines()
        } catch (e: IOException) {
            throw e
        }

        val contents = lines.joinToString("")

        setup(filename, lines)

        return Pair(contents, lines)
    }

    fun setup(filename: String, lines: List<String>) {
        FILENAME = filename
        LINES = lines
        Report.setup(lines.size)
    }

    fun lexeme(at: Loc): String {
        val (begin, end) = at.extremes().map(UInt::indexable)

        return at.line().substring(begin..end)
    }

    fun line(at: Loc) = try {
        LINES[at.line.indexable()]
    } catch (e: IndexOutOfBoundsException) {
        ""
    }
}