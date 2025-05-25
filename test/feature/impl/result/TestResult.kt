package feature.impl.result

import feature.impl.fail.FailStage

/**
 * Represents a result of a test.
 *
 * It stores the list of file IDs that failed the test, along with a [FailStage] denoting the stage at which they
 * failed.
 */
data class TestResult(
    private val failures: Map<Int, FailStage> = mapOf()
) {
}