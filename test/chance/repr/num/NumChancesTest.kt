package chance.repr.num

import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestTemplate

class NumChancesTest {
    @Test
    fun testOne() {
        assertTrue(NumChances.real() includes 10.toDouble())
    }

    @Test
    fun testTwo() {
        assertTrue(NumChances.real() includes NumChances.of(0..10))
    }

    @Test
    fun testThree() {
        assertTrue(NumChances.real() includes NumChances.real())
    }

    @Test
    fun testFour() {
        assertTrue(
            NumRange(Edge.Excl(-12.0), Edge.Incl(30.0)) includes
            NumRange(Edge.Incl(-11.99999), Edge.Excl(30.0))
        )
    }

    @Test
    fun testFive() {
        assertEquals(
            NumChances(listOf(
                NumRange.single(4.0), NumRange.single(11.0)
            )),
            NumChances(listOf(
                NumRange.single(3.0), NumRange.single(10.0)
            )).map { a -> a + 1 }
        )
    }
}