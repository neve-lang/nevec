package file.module

import err.report.Report

data class Module(val name: String) {
    companion object {
        fun curr() = Module(Report.FILENAME)

        fun prelude() = Module("prelude")
    }
}