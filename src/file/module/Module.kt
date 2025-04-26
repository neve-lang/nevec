package file.module

import file.contents.Src

/**
 * Simple wrapper around the concept of a module.
 *
 * NOTE: This is a loose definition for now; things will change as we progress in versions.
 */
data class Module(val name: String) {
    companion object {
        /**
         * Represents Neve’s “prelude” module, being the standard library.
         */
        val PRELUDE = Module("prelude")

        /**
         * Represents the current [Module].
         */
        fun curr(): Module {
            return Module(Src.FILENAME)
        }
    }
}