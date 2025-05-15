package feature.culprits

import feature.FeatureTest
import org.junit.jupiter.api.Test

class CulpritsTest {
    @Test
    fun test() {
        assert(FeatureTest("culprits").succeeds())
    }
}