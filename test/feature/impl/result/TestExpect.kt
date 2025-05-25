package feature.impl.result

import feature.impl.file.FileId
import feature.impl.outcome.Outcome

/**
 * Similarly to [TestResult], it represents an **expected** map of [Outcome] that gets compared to the
 * actually produced [TestResult].
 */
data class TestExpect(
    private val expected: Map<FileId, Outcome> = mapOf()
) {
}