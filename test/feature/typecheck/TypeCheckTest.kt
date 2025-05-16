package feature.typecheck

import feature.FeatureTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TypeCheckTest {
    @Test
    fun test() {
        assertFalse(FeatureTest("typecheck").succeeds())
    }
}