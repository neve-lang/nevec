package parse

import ctx.Ctx
import pretty.Pretty
import file.contents.Src
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParseTest {
    @Test
    fun testOne() {
        assertOkay("(1 + 2 == 3)")
    }

    @Test
    fun testTwo() {
        assertOkay("---not --not -not (1 + 2 + 3 < (4 > 2) * 3)")
    }

    @Test
    fun testThree() {
        assertOkay("1 bitor 2 xor 3849348 / 23 * 9 + nil / true bitand false")
    }

    @Test
    fun testFour() {
        assertOkay("1 bitor \"Hello, world!\" + -34.5 / 92 xor \"()\"")
    }

    @Test
    fun testFive() {
        assertOkay("\"Hello, #{\"world!\"}  From #{\"Mars!\"}  This message took #{1.5 * 2} minutes to #{\"reach #{\"Earth\"}.\"}\"")
    }

    @Test
    fun testSix() {
        assertOkay(
            "[\"My\": 10, \"Table\": 20, \"Is\": 30, \"Awesome\": 40]"
        )
    }

    @Test
    fun testSeven() {
        assertOkay(
            "print 10 + 2\n" +
            "print (10 + 2 == 2)\n" +
            "print (10 + 2 == 2)\n" +
            "print [\"My\": 10, \"Table\": 20, \"Is\": 30, \"Awesome\": 40]"
        )
    }

    private fun assertOkay(input: String) {
        assertEquals(input, input.parse())
    }
}

fun String.parse(): String {
    Src.setup("test.neve", lines())

    val parse = Parse(this, Ctx.test())
    val pretty = Pretty.visit(parse.parse())
    return pretty
}