package file

import err.report.Report
import java.io.File
import java.io.IOException

object Src {
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