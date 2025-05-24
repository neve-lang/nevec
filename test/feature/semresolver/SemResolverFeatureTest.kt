package feature.semresolver

import feature.FeatureTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class SemResolverFeatureTest {
    @Test
    fun test() {
        assertFalse(FeatureTest("semresolver").succeeds())
    }
}