package chance.repr.bool

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class BoolChancesTest {
    @Test
    fun testOne() {
        assertTrue(BoolChances.from(true).into())
    }

    @Test
    fun testTwo() {
        assertFalse(BoolChances.from(false).into())
    }

    @Test
    fun testThree() {
        assertFalse(BoolChances.undecidable().into())
    }

    @Test
    fun testFour() {
        assertTrue(BoolChances.undecidable().map { a -> !a }.into())
    }

    @Test
    fun testFive() {
        assertFalse(BoolChances.from(true).map { a -> !a }.into())
    }
}