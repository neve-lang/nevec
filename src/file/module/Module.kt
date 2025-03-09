package file.module

import err.report.Report

/**
 * Simple wrapper around the concept of a module.
 */
data class Module(val name: String) {
    companion object {
        fun curr() = Module(Report.FILENAME)

        fun prelude() = Module("prelude")
    }
}