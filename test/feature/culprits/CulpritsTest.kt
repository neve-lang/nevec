package feature.culprits

import feature.FeatureTest
import org.junit.jupiter.api.Test
import type.poison.Poison

class CulpritsTest {
    @Test
    fun test() {
        // we’re getting some really weird behavior here—it seems like, if I remove this tiny statement,
        // Poison.NAMES goes wild and sets its "Unknown" entry to `null`, even though it’s a private field and nothing
        // tells it to do that anywhere.
        Poison.fromName("")

        assert(FeatureTest("culprits").succeeds())
    }
}