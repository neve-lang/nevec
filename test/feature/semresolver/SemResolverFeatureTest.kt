package feature.semresolver

import feature.impl.FeatureTest
import feature.impl.result.ExpectMap
import feature.impl.result.TestExpect
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class SemResolverFeatureTest {
    @Test
    fun test() {
        val parseExpect = TestExpect.failsAt(
            testCount = 19,
            4, 6, 7, 9, 11, 14, 15, 16
        )

        assertFalse(FeatureTest(
            "semresolver",
            ExpectMap.forParseTests(parseExpect)
        ).check())
    }
}