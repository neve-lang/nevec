package lex

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import tok.Tok
import tok.TokKind.*

class LexTest {
    @Test
    fun testOne() {
        assertEquals(
            listOf(
                LET, ID, ASSIGN, STR, NEWLINE, ID, ID, EQ, STR, EOF
            ), "let my_name = \"Name\"\nputs my_name == \"Name\"".lex()
        )
    }

    @Test
    fun testInvalid() {
        assertEquals(
            listOf(
                LET, ERR, ASSIGN, INT, EOF
            ), "let @ = 42".lex()
        )
    }

    @Test
    fun testComplex() {
        assertEquals(
            listOf(
                ID, PLUS, ID, STAR, LPAREN, ID, MINUS, INT, RPAREN, EOF
            ), "x + y * (z - 1)".lex()
        )
    }

    @Test
    fun testComment() {
        assertEquals(
            listOf(
                LET, ID, ASSIGN, INT, EOF
            ), "# This shouldn't affect the tokenization process\nlet x = 42".lex()
        )
    }

    @Test
    fun testCommentTwo() {
        assertEquals(
            "#".lex(), listOf(EOF)
        )
    }

    @Test
    fun testEmpty() {
        assertEquals("".lex(), listOf(EOF))
    }

    @Test
    fun testInterpol() {
        assertEquals(
            listOf(
                LET, ID, ASSIGN, INTERPOL, INTERPOL, STR, INTERPOL_SEP, STR, INTERPOL_SEP, STR, EOF
            ), "let greeting = \"Hello, #{\"world!  How are #{\"you\"}? \"}\"".lex()
        )
    }

    @Test
    fun testEmptyInterpol() {
        assertEquals(
            listOf(
                LET, ID, ASSIGN, ERR, STR, EOF
            ), "let greeting = \"Hello, #{}!\"".lex()
        )
    }

    @Test
    fun testUnterminatedString() {
        assertEquals(
            listOf(
                LET, ID, ASSIGN, ERR, EOF
            ), "let greeting = \"Hello, world!".lex()
        )
    }

    @Test
    fun testFloat() {
        assertEquals(
            listOf(
                FLOAT,
                NEWLINE,
                FLOAT,
                NEWLINE,
                ERR,
                NEWLINE,
                INT,
                DOT,
                ID,
                NEWLINE,
                FLOAT,
                DOT,
                ID,
                NEWLINE,
                ERR,
                DOT,
                ID,
                EOF
            ), ".09\n1.23\n1.2.3\n1.times\n1.2.times\n1.2.3.times".lex()
        )
    }

    @Test
    fun testSemicol() {
        assertEquals(
            listOf(
                LET,
                ID,
                ASSIGN,
                DO,
                NEWLINE,
                ID,
                STR,
                NEWLINE,
                END,
                NEWLINE,
                ID,
                SEMICOL,
                ID,
                SEMICOL,
                ID,
                SEMICOL,
                ID,
                EOF
            ), "let a = do\n  puts \"Hello, world!\"\nend\na; a; a; a".lex()
        )
    }

    @Test
    fun testUnicode() {
        assertEquals(
            listOf(
                STR, STR, EOF
            ), "\"👋 Hello \" \"world!\"".lex()
        )
    }
}

fun Lex.all(): List<Tok> {
    val tokInList = listOf(next())

    if (tokInList.first().isEof()) {
        return tokInList
    }

    return tokInList + all()
}

fun List<Tok>.simplified() = map { it.kind }

fun String.lex() = Lex(this).all().simplified()