package feature.impl.routine

import cli.CliOptions
import feature.impl.outcome.Outcome
import nevec.Nevec

/**
 * Describes a specific “test routine” to be applied to a specific test folder.
 *
 * For example:
 *
 * - Tests in the `parse/` folder may be run with the [`--check-only`][cli.Options.CHECK_ONLY] CLI option
 * - Whereas tests in the `runtime/` folder may have a more complex **test routine**: including running a first test
 *   without `valgrind`, running another with it, running the native binary, etc.
 */
data class TestRoutine(private val routine: (filename: String) -> Outcome) {
    companion object {
        /**
         * @return A default [TestRoutine] that usually applies to tests in the `parse/` folder.
         */
        fun forParseTests(): TestRoutine {
            return TestRoutine {
                filename -> Outcome.from(
                    Nevec.runWithOptions(filename, options = CliOptions.test())
                )
            }
        }
    }
}