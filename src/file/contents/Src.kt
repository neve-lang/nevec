package file.contents

import err.report.Report
import java.io.File
import java.io.IOException

/**
 * Abstraction layer around setting up [Report] and reading a file.
 */
object Src {
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

        Report.setup(filename, lines)

        return Pair(contents, lines)
    }
}