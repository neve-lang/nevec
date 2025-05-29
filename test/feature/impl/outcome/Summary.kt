package feature.impl.outcome

import feature.impl.file.FileId

/**
 * Maps each different [Outcome] to a list of [FileIds][FileId], and provides a summary message via the [msg] method.
 *
 * A [Summary] may **only** store the **discrepancies** between the expected results **and** the actual results.
 * When no discrepancies are found, [fails] must be empty.
 */
data class Summary(private val fails: Map<Outcome, List<FileId>>) {
    /**
     * @return Whether [fails] is empty, which implies that no discrepancies exist.
     */
    fun hasDiscrepancies(): Boolean {
        return fails.isEmpty()
    }

    /**
     * @return A [String] summary message of the test.
     */
    fun msg(): String {
        val entries = fails.entries.map { it.toPair() }.toList()

        return "summary:\n${formatFails(entries)}"
    }

    private fun formatFails(entries: List<Pair<Outcome, List<FileId>>>): String {
        return if (entries.isEmpty())
            " → all tests passed!"
        else
            entries.joinToString(separator = "\n") {
                (outcome, files) -> " → tests n. ${files.joinToString()} ${formatOutcome(outcome)}"
            }
    }

    private fun formatOutcome(outcome: Outcome) = when (outcome) {
        is Outcome.Success -> "succeeded, when they should’ve failed."
        is Outcome.Fail -> "failed by ${outcome.stage}."
    }
}