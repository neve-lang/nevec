package file.contents

import err.report.Report
import file.span.Loc
import file.span.indexable
import util.extension.map
import java.io.File
import java.io.IOException

/**
 * Abstraction layer around setting up [Report] and reading a file.
 *
 * NOTE: This object is temporary.  Once we implement a module system, we will use
 * a different management system.
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

        val contents = lines.joinToString("\n")
        setup(filename, lines)

        return Pair(contents, lines)
    }

    /**
     * Sets the values of [FILENAME] and [LINES] to [filename] and [lines], respectively.
     *
     * It then sets up [Report] with `lines.size`.
     */
    fun setup(filename: String, lines: List<String>) {
        FILENAME = filename
        LINES = lines
        Report.setup(lines.size)
    }

    /**
     * Takes a [Loc] and takes a substring of the source code based on it.
     *
     * @param at the [Loc] in question.
     *
     * @return a lexeme [String] based on a [Loc].
     */
    fun lexeme(at: Loc): String {
        val (begin, end) = at.extremes().map(UInt::indexable)

        return at.line().substring(begin..end)
    }

    /**
     * @param at The [Loc] in question.
     *
     * @return a line at the [Loc]’s line, or an empty string if the [Loc] is out of bounds.
     */
    fun line(at: Loc) = try {
        LINES[at.line.indexable()]
    } catch (e: IndexOutOfBoundsException) {
        ""
    }
}