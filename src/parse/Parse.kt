package parse

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.Operator
import ast.hierarchy.decl.Decl
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.unop.UnOp
import err.msg.Msg
import file.span.Loc
import parse.binop.AnyBinOp
import parse.err.ParseErr
import parse.state.ParseState
import parse.tok.Window
import tok.Tok
import tok.TokKind
import util.extension.until

/**
 * Takes the source code and parses it.
 *
 * No prior lexical analysis is required--the Lexer works alongside the Parser.
 *
 * @param contents The contents of the source file.
 */
class Parse(contents: String) {
    private val window = Window(contents)
    private val state = ParseState()

    init {
        consume()
    }

    fun hadErr(): Boolean {
        return state.hadErr
    }

    fun parse(): Program {
        val decls = until(::isAtEnd, ::decl)

        if (hadErr()) {
            return Program.empty()
        }

        return Program(decls)
    }

    private fun decl() = when (kind()) {
        TokKind.CONST -> constsDecl()
        else -> stmt().wrap()
    }

    private fun constsDecl(): Decl.Consts {
        consume()

        val scope = if (hadNewline()) null
        else consume(TokKind.ID)?.lexeme

        val members = until({ check(TokKind.END) }, {
            val name = consume(TokKind.ID)
            val loc = name?.loc ?: Loc.new()
            val lexeme = name?.lexeme ?: ""

            // TODO: implement type hinting: `Thing Int`
            expect(TokKind.ASSIGN)

            val expr = expr()

            Decl.Const(lexeme, expr, loc, expr.type())
        })

        consume(TokKind.END)
        return Decl.Consts(scope, members)
    }

    private fun stmt() = when (kind()) {
        TokKind.PRINT -> printStmt()
        else -> expr().wrap()
    }

    private fun printStmt(): Stmt.Print {
        val tok = consume()
        val expr = expr()

        return Stmt.Print(tok.loc, expr)
    }

    private fun expr(): Expr {
        return bitOr()
    }

    private fun bitOr(): Expr {
        return binOp(AnyBinOp::bitwise, ::bitXor, TokKind.BIT_OR)
    }

    private fun bitXor(): Expr {
        return binOp(AnyBinOp::bitwise, ::bitAnd, TokKind.BIT_XOR)
    }

    private fun bitAnd(): Expr {
        return binOp(AnyBinOp::bitwise, ::eq, TokKind.BIT_AND)
    }

    private fun eq(): Expr {
        return binOp(AnyBinOp::comp, ::comp, TokKind.EQ, TokKind.NEQ)
    }

    private fun comp(): Expr {
        return binOp(AnyBinOp::comp, ::bitShift, TokKind.GT, TokKind.GTE, TokKind.LT, TokKind.LTE)
    }

    private fun bitShift(): Expr {
        return binOp(AnyBinOp::bitwise, ::term, TokKind.SHL, TokKind.SHR)
    }

    private fun term(): Expr {
        return binOp(AnyBinOp::arith, ::factor, TokKind.PLUS, TokKind.MINUS)
    }

    private fun factor(): Expr {
        return binOp(AnyBinOp::arith, ::unary, TokKind.STAR, TokKind.SLASH)
    }

    private fun binOp(into: (AnyBinOp) -> BinOp, child: () -> Expr, vararg kinds: TokKind): Expr {
        var left = child()

        while (check(*kinds)) {
            val op = consume()
            val right = child()

            // using '!!' here because 'op.kind' is defined to be valid
            val operator = Operator.from(op)!!

            val loc = (left.loc() + right.loc()).build()
            left = into(AnyBinOp(loc, left, operator, right)).wrap()
        }

        return left
    }

    private fun unary(): Expr {
        if (!check(TokKind.MINUS, TokKind.NOT)) {
            return call()
        }

        val op = consume()
        val operand = unary()

        val loc = op.loc.tryMerge(with = operand.loc())
        return when (op.kind) {
            TokKind.NOT -> UnOp.Not(operand, loc).wrap()
            TokKind.MINUS -> UnOp.Neg(operand, loc).wrap()
            // this, by definition, shouldn't ever happen--check() restricts our possible types down to TokKind.MINUS
            // and TokKind.NOT.
            else -> Expr.Empty(here())
        }
    }

    private fun call(): Expr {
        return primary()
    }

    private fun primary() = when (kind()) {
        TokKind.LPAREN -> grouped()

        TokKind.ID -> Expr.Access(consume())
        TokKind.TRUE -> Lit.BoolLit(true, consumeHere()).wrap()
        TokKind.FALSE -> Lit.BoolLit(false, consumeHere()).wrap()
        TokKind.NIL -> Lit.NilLit(consumeHere()).wrap()

        TokKind.INT -> intLit().wrap()
        TokKind.FLOAT -> floatLit().wrap()
        TokKind.LBRACKET -> listOrTable().wrap()
        TokKind.STR -> strLit().wrap()
        TokKind.INTERPOL -> interpol().wrap()

        else -> Expr.Empty(here()).also { showMsg(ParseErr.expectedExpr(consume())) }
    }

    private fun intLit(): Lit.IntLit {
        return consume().let { Lit.IntLit(value = it.lexeme.toInt(), it.loc) }
    }

    private fun floatLit(): Lit.FloatLit {
        return consume().let { Lit.FloatLit(value = it.lexeme.toFloat(), it.loc) }
    }

    private fun strLit(): Lit.StrLit {
        return consume().let { Lit.StrLit(value = stringOf(it), it.loc) }
    }

    private fun grouped(): Expr.Parens {
        val from = consume().loc
        val expr = expr()
        val to = consume(TokKind.RPAREN)?.loc

        return Expr.Parens(expr, from.tryMerge(with = to))
    }

    private fun interpol(): Interpol {
        if (check(TokKind.STR)) {
            return Interpol.End(stringOf(curr()), consumeHere())
        }

        val begin = here()
        val string = stringOf(consume())

        val expr = expr()
        consume(TokKind.INTERPOL_SEP)
        val next = interpol()

        val loc = begin.tryMerge(with = next.loc())
        return Interpol.Some(string, expr, next, loc)
    }

    private fun listOrTable(): Lit {
        val leftBracket = consume().loc

        if (match(TokKind.COL)) {
            return emptyTable(leftBracket)
        }

        val firstExpr = expr()

        // unconditional now--support for lists will come later
        consume(TokKind.COL)
        return table(leftBracket, firstExpr)
    }

    private fun emptyTable(leftBracket: Loc): Lit.TableLit {
        val rightBracket = consume(TokKind.RBRACKET)?.loc
        val loc = leftBracket.tryMerge(with = rightBracket)

        return Lit.TableLit(emptyList(), emptyList(), loc)
    }

    private fun table(leftBracket: Loc, firstKey: Expr): Lit.TableLit {
        val firstVal = expr()

        // val (keys, vals) = Rule(this).whileHas(TokKind.COL)
        // .consume(::expr).sepBy(TokKind.COL)
        // .then(TokKind.RBRACKET).take()

        val keys = mutableListOf(firstKey)
        val vals = mutableListOf(firstVal)

        while (match(TokKind.COL)) {
            keys.add(expr())
            consume(TokKind.COL)
            vals.add(expr())
        }

        val rightBracket = consume(TokKind.RBRACKET)?.loc
        val loc = leftBracket.tryMerge(with = rightBracket)

        return Lit.TableLit(keys, vals, loc)
    }

    private fun sync() {
        state.relax()

        while (!isAtEnd()) {
            if (hadNewline() || kind().isStmtStarter()) {
                return
            }

            consume()
        }
    }

    private fun expect(kind: TokKind) {
        if (check(kind)) {
            consume()
            return
        }

        if (check(TokKind.EOF, TokKind.NEWLINE)) {
            showMsg(ParseErr.expectedTok(here(), kind))
            return
        }

        showMsg(ParseErr.unexpectedTok(curr(), kind))
    }

    private fun showMsg(msg: Msg) {
        if (state.isPanicking) {
            return
        }

        state.markErr()
        msg.print()
    }

    private fun consume(kind: TokKind): Tok? {
        if (!check(kind)) {
            expect(kind)
            return null
        }

        return consume()
    }

    private fun consume(): Tok {
        val tok = window.advance()

        if (tok.isErr()) {
            showMsg(ParseErr.unexpectedChar(tok))
        }

        return tok
    }

    private fun funCallWithoutParens() = kind().isExprStarter() && !hadNewline()

    // in Neve, we would have an additional restriction here:
    // fun raw_string_of(tok StrTok)
    // with StrTok = Tok where self.kind in [TokKind.Str, TokKind.Interpol]
    private fun stringOf(tok: Tok) = tok.lexeme

    private fun check(vararg kinds: TokKind) = window.check(*kinds)

    private fun match(vararg kinds: TokKind) = window.match(*kinds)

    private fun consumeHere() = here().also { consume() }

    private fun hadNewline() = window.hadNewline()

    private fun isAtEnd() = window.isAtEnd()

    private fun curr() = window.curr

    private fun here() = window.here()

    private fun kind() = window.kind()
}