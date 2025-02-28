package err.report

import err.write.Color
import err.write.Out
import err.write.Write

object Report {
    private lateinit var FILENAME: String
    private lateinit var LINES: List<String>

    fun setup(filename: String, lines: List<String>) {
        FILENAME = filename
        LINES = lines
    }

    fun fileErr(filename: String) {
        Write.paintedIn(Color.RED).saying(" × ").then().saying("could not read '$filename'").print(Out.fatal())
    }
}