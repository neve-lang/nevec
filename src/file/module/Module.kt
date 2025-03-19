package file.module

import file.contents.Src

/**
 * Simple wrapper around the concept of a module.
 */
data class Module(val name: String) {
    companion object {
        fun curr() = Module(Src.FILENAME)

        fun prelude() = Module("prelude")
    }
}