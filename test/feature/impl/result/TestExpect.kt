package feature.impl.result

import feature.impl.file.FileId
import feature.impl.outcome.ExecFail
import feature.impl.outcome.Outcome
import feature.impl.outcome.Summary

/**
 * Similarly to [TestResult], it represents an **expected** map of [Outcome] that gets compared to the
 * actually produced [TestResult].
 */
data class TestExpect(
    private val expected: Map<FileId, Outcome> = mapOf()
) {
    companion object {
        /**
         * @return A new [TestExpect] whose [expected] has the following properties:
         *
         * - For the file IDs mentioned, those will be mapped to an [Outcome] of
         *   [Success][Outcome.Success] failure.
         * - The non-mentioned ones will, up to the given [testCount], be implicitly given an [Outcome] of
         *   [COMPILE][feature.impl.outcome.ExecFail.COMPILE]
         *
         * @throws IllegalArgumentException if [testCount] is less than `0`.
         */
        fun successesAt(testCount: Int, vararg successes: FileId): TestExpect {
            return forall(testCount) {
                Outcome.basedOn(whether = it in successes)
            }
        }

        /**
         * @return A new [TestExpect] whose [expected] has the following properties:
         *
         * - For the file IDs mentioned, those will be mapped to an [Outcome] of
         *   [COMPILE][feature.impl.outcome.ExecFail.COMPILE] failure.
         * - The non-mentioned ones will, up to the given [testCount], be implicitly given an [Outcome] of
         *   [Success][Outcome.Success].
         *
         * @throws IllegalArgumentException if [testCount] is less than `0`.
         */
        fun failsAt(testCount: Int, vararg failures: FileId): TestExpect {
            return forall(testCount) {
                Outcome.basedOn(whether = it !in failures)
            }
        }

        /**
         * @return A new [TestExpect] that maps all [FileIds][FileId], from `0` to [testCount], to [Outcome.OfFail].
         * The [ExecFail] stage given will be [ExecFail.COMPILE]
         */
        fun allFail(testCount: Int): TestExpect {
            return forall(testCount) {
                Outcome.OfFail(stage = ExecFail.COMPILE)
            }
        }

        private fun forall(testCount: Int, f: (Int) -> Outcome): TestExpect {
            require(testCount >= 0) {
                "Constructing a `TestExpect` requires a strictly positive `testCount`."
            }

            return (0..testCount).associateWith {
                f(it)
            }.toMap().let(::TestExpect)
        }
    }

    /**
     * Compares `this` [TestExpect] with a [TestResult].
     *
     * @return A [Summary] with the discrepancies stored there.
     */
    fun compare(with: TestResult): Summary {
        return discrepancies(expected, with.outcomes)
            .groupBy { (_, outcome) -> outcome }
            .mapValues { (_, grouped) -> grouped.map { it.first } }
            .let(::Summary)
    }

    private fun discrepancies(
        a: Map<FileId, Outcome>,
        b: Map<FileId, Outcome>
    ): List<Pair<FileId, Outcome>> {
        val aAsList = a.toList()
        val bAsList = b.toList()

        return aAsList.associateBy { (fileId, _) -> fileId }.let { map ->
            bAsList.filter { (fileId, outcome) ->
                outcome != map[fileId]?.second
            }
        }
    }
}