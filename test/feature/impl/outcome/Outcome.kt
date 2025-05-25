package feature.impl.outcome

/**
 * Represents a possible outcome when executing a test.
 *
 * It can either be:
 *
 * - Successful: [Success]
 * - Unsuccessful: [Fail]
 *
 * Keep in mind that an unsuccessful outcome **does not imply** that the **test itself failed**—it is perfectly common
 * to expect a test to fail.
 */
sealed class Outcome {
    /**
     * Represents a successful test execution.
     */
    data object Success : Outcome()

    /**
     * Represents an unsuccessful test execution, containing a [ExecFail].
     */
    data class Fail(val stage: ExecFail) : Outcome()
}