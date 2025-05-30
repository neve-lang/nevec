package ctx

import cli.CliOptions
import cli.Options

/**
 * The Neve compiler’s **context object**.
 *
 * Stores data regarding compiler settings and command-line arguments.
 *
 * @property options The CLI options the program was launched with.
 */
data class Ctx(val options: CliOptions) {
    companion object {
        /**
         * @return A [Ctx] context data class with [options][CliOptions] corresponding to a compiler test.
         *
         * @see CliOptions
         * @see CliOptions.checkTest
         */
        fun checkTest(): Ctx {
            return Ctx(CliOptions.checkTest())
        }
    }

    /**
     * @return Whether `this` [Ctx]’s options has the given [option] enabled.
     */
    fun isEnabled(option: Options): Boolean {
        return options.isEnabled(option)
    }
}