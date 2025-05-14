package hook

import nevec.Nevec
import nevec.result.Aftermath

/**
 * Hooks the feature test module to the compiler, by providing an [okay] method that returns `true` if compilation
 * was successful, or `false` otherwise.
 */
object TestHook {
    /**
     * Takes in a list of CLI arguments ([args]), passes them to [Nevec] and initiates the compilation stage.
     *
     * @return `true` if [Nevec.run] returns an [Aftermath.Success], `false` otherwise.
     *
     * @see Aftermath
     */
    fun okay(args: Array<String>): Boolean {
        return Nevec.run(args) is Aftermath.Success
    }
}