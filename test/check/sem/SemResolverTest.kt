package check.sem

import ast.hierarchy.program.Program
import ctx.Ctx
import file.contents.Src
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import parse.Parse
import pretty.Pretty

class SemResolverTest {
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
        val parsed = input.parse()

        assertEquals(input, parsed.pretty())
        assertEquals(parsed.pretty(), parsed.resolved().pretty())
    }
}

fun String.parse(): Program {
    Src.setup("test.neve", lines())

    return Parse(this, Ctx.checkTest()).parse()
}

fun Program.resolved(): Program {
    return SemResolver().visit(this)
}

fun Program.pretty(): String {
    return Pretty.visit(this)
}