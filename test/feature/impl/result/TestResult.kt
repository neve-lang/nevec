package feature.impl.result

import feature.impl.file.FileId
import feature.impl.outcome.Outcome

/**
 * Represents a result of a test pass.
 *
 * It stores a map of [FileId] and [Outcome], mapping each test file to its respective outcome.
 */
data class TestResult(
    val outcomes: Map<FileId, Outcome> = mapOf()
) {
    companion object {
        /**
         * @return A new [TestResult] built from a list of [pairs].
         */
        fun from(pairs: List<Pair<FileId, Outcome>>): TestResult {
            return TestResult(pairs.toMap())
        }
    }
}