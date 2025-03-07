package lex

import file.contents.Chars
import file.span.Loc
import lex.interpol.InterpolState
import lex.interpol.InterpolTooDeepException
import tok.Tok
import tok.TokKind

class Lex(contents: String) {
    private val chars = Chars(contents)
    private val loc = Loc.new()
    private val captured = mutableListOf<Char>()
    private val state = InterpolState()

    fun next(): Tok {
        skipWs()

        if (isAtEnd()) {
            sync()
            return newTok(TokKind.EOF)
        }

        if (state.wasInInterpol()) {
            return string(captureFirst = false)
        }

        if (isOnDigit() || isOnFloat()) {
            return number()
        }

        if (isOnAlpha()) {
            return id()
        }

        // using '!!' because !isAtEnd() <=> chars.peek() != null
        return when (peek()!!) {
            '"' -> string()
            '}' -> endInterpol()
            '\n' -> newline()
            else -> simpleTok()
        }
    }

    private fun string(captureFirst: Boolean = true): Tok {
        if (!captureFirst) {
            sync()
        } else {
            advance()
        }

        while (!check('"')) {
            if (matchSeq("#{")) {
                return interpol()
            }

            if (check('\n')) {
                loc.newline()
            }

            advance()
        }

        if (isAtEnd()) {
            return err("unterminated string")
        }

        advance()
        return newTok(TokKind.STR)
    }

    private fun interpol(): Tok {
        try {
            state.deepenInterpol()
        } catch (e: InterpolTooDeepException) {
            return err("maximum string interpolation depth exceeded")
        }

        val tok = newTok(TokKind.INTERPOL)

        sync()

        // skip the '#{'
        advance()
        advance()

        if (match('}')) {
            state.endInterpol()
            return err("empty string interpolation")
        }

        return tok
    }

    private fun endInterpol(): Tok {
        advance()

        if (!state.inInterpol()) {
            return err("'}' outside string interpolation")
        }

        state.endInterpol()
        return newTok(TokKind.INTERPOL_SEP)
    }

    private fun number(): Tok {
        takeWhile { isOnDigit() }

        val isFloat = isOnFloat()
        if (isFloat) {
            advance()
            takeWhile { isOnDigit() }
        }

        // still isOnFloat() <=> something like 1.2.3
        if (isOnFloat()) {
            sync()
            advance()

            takeWhile { isOnDigit() }
            return err("invalid floating point number")
        }

        return newTok(if (isFloat) TokKind.FLOAT else TokKind.INT)
    }

    private fun id(): Tok {
        takeWhile { isOnAlpha() }

        val kind = Toks.findKeyword(lexeme()) ?: TokKind.ID
        return newTok(kind)
    }

    private fun simpleTok(len: Int = Toks.MAX_TOK_LEN): Tok {
        if (len == 0) {
            advance()
            return err("invalid character")
        }

        val peephole = peek(len) ?: return simpleTok(len - 1)
        val kind = Toks.findTok(peephole) ?: return simpleTok(len - 1)

        capture(len)
        return newTok(kind)
    }

    private fun newline(): Tok {
        advance()
        loc.newline()

        return newTok(TokKind.NEWLINE)
    }

    private fun advance(): Char? {
        val char = chars.next()

        capture(char)
        loc.advance()

        return char
    }

    private fun sync() {
        captured.clear()
        loc.sync()
    }

    private fun skipWs() {
        if (match('#')) {
            skipComment()
        }

        takeWhile { it.isInsignificant() }
        sync()
    }

    private fun skipComment() {
        takeWhile { it != '\n' }

        advance()
        loc.newline()
    }

    private fun takeWhile(predicate: ((Char) -> Boolean)) {
        // using '!!' here because isAtEnd() <=> chars.peek() == null
        if (isAtEnd() || !predicate(peek()!!)) {
            return
        }

        advance()
        takeWhile(predicate)
    }

    private fun capture(c: Char?) {
        captured.add(c ?: return)
    }

    private fun capture(n: Int) {
        if (n > 0) {
            advance()
            capture(n - 1)
        }
    }

    private fun match(against: Char): Boolean {
        if (check(against)) {
            advance()
            return true
        }

        return false
    }

    private fun lexeme() = captured.joinToString("")

    private fun peek() = chars.peek()

    private fun peek(n: Int) = chars.peek(n)

    private fun peekNext() = chars.peek(2)?.last()

    private fun matchSeq(seq: String) = chars.peek(seq.length) == seq

    private fun check(against: Char) = peek() == against || isAtEnd()

    private fun check(predicate: (Char) -> Boolean) = peek()?.let { predicate(it) } ?: false

    private fun isAtEnd() = peek() == null

    private fun isOnAlpha() = check(Char::isLetter) || check('_')

    private fun isOnDigit() = check(Char::isDigit)

    private fun isOnFloat() = check('.') && peekNext()?.isDigit() ?: false

    private fun newTok(kind: TokKind) = Tok(kind, lexeme(), loc.copy())

    private fun err(msg: String) = Tok(TokKind.ERR, msg, loc.copy())
}

fun Char.isInsignificant() = this != '\n' && this.isWhitespace()