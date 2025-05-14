package feature.semresolver

import feature.FeatureTest
import org.junit.jupiter.api.Test

class SemResolverFeatureTest {
    @Test
    fun test() {
        assert(FeatureTest("semresolver").run())
    }
}