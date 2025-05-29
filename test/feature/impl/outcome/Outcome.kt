package feature.impl.outcome

import nevec.result.Aftermath

/**
 * Represents a possible outcome when executing a test.
 *
 * It can either be:
 *
 * - Successful: [Success]
 * - Unsuccessful: [Fail]
 *
 * Keep in mind that an unsuccessful outcome **does not imply** that the **test itself failed**—it is perfectly common
 * to expect a program’s compilation to fail.
 */
sealed class Outcome {
    companion object {
        /**
         * @return A new [Outcome] from an [Aftermath] data class.
         */
        fun from(aftermath: Aftermath) = when (aftermath) {
            is Aftermath.Success -> Success
            is Aftermath.OfFail -> Fail(stage = ExecFail.from(aftermath))

            // IntelliJ seems to complain here for some reason, even though all cases are covered?
            else -> TODO()
        }

        /**
         * @return An [Outcome] from a [Boolean].  That is:
         *
         * - If the value given is `false`, An [Outcome] of [Fail] whose stage is [ExecFail.COMPILE] is returned.
         * - Otherwise, an [Outcome] of [Success] is returned.
        */
        fun basedOn(whether: Boolean): Outcome {
            return if (whether)
                Success
            else
                Fail(stage = ExecFail.COMPILE)
        }
    }

    /**
     * Represents a successful test execution.
     */
    data object Success : Outcome()

    /**
     * Represents an unsuccessful test execution, containing a [ExecFail].
     */
    data class Fail(val stage: ExecFail) : Outcome()
}