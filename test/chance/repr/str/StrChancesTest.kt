package chance.repr.str

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class StrChancesTest {
    companion object {
        fun daysOfTheWeek() =
            StrChances.from("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    }

    @Test
    fun testOne() {
        assertTrue(daysOfTheWeek() includes "Monday")
    }

    @Test
    fun testTwo() {
        assertFalse(daysOfTheWeek() includes "monday")
    }

    @Test
    fun testThree() {
        assertTrue(daysOfTheWeek().map { it.lowercase() } includes "monday")
    }

    @Test
    fun testFour() {
        assertEquals(
            daysOfTheWeek(),
            daysOfTheWeek()
        )
    }

    @Test
    fun testFive() {
        assertNotEquals(
            StrChances.Every,
            daysOfTheWeek()
        )
    }

    @Test
    fun testSix() {
        assertTrue(
            StrChances.Every includes "Monday"
        )
    }
}