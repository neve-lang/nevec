package feature.typecheck

import feature.impl.FeatureTest
import feature.impl.result.ExpectMap
import feature.impl.result.TestExpect
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TypeCheckTest {
    @Test
    fun test() {
        val parseExpect = TestExpect.successesAt(
            testCount = 11,
            9
        )

        assertFalse(FeatureTest(
            "typecheck",
            ExpectMap.forParseTests(parseExpect)
        ).check())
    }
}