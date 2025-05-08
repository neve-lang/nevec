package parse

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.operator.Operator
import ast.hierarchy.expr.Expr
import ast.hierarchy.interpol.Interpol
import ast.hierarchy.lit.Lit
import ast.hierarchy.program.Program
import ast.hierarchy.stmt.Stmt
import ast.hierarchy.unop.UnOp
import ast.info.Info
import ctx.Ctx
import err.msg.Msg
import file.span.Loc
import meta.Meta
import meta.result.MetaResult
import meta.target.Target
import parse.binop.AnyBinOp
import parse.err.ParseErr
import parse.ctx.ParseCtx
import parse.meta.ParseMeta
import parse.state.ParseState
import parse.tok.Window
import tok.Tok
import tok.TokKind
import type.table.TypeTable
import util.extension.until

/**
 * Takes the source code and parses it.
 *
 * No prior lexical analysis is required — the Lexer works alongside the Parser.
 *
 * @param contents The contents of the source file.
 */
class Parse(contents: String, private val ctx: Ctx) {
    private val window = Window(contents)
    private val state = ParseState()

    // right now, we just assume we have access to the TypeTable.  once we implement a symbol resolution phase,
    // we will refactor this.
    private val typeTable = TypeTable()

    init {
        consume()
    }

    /**
     * Parses the source contents given.
     *
     * @return A regular [Program] node if no errors occurred, an empty [Program] node otherwise.
     *
     * Note that this behavior may be updated in the future.
     *
     * @see Program
     */
    fun parse(): Program {
        val decls = until(::isAtEnd, ::decl)

        if (hadErr()) {
            return Program.empty()
        }

        return Program(decls)
    }

    private fun decl() = when (kind()) {
        else -> stmt().wrap()
    }

    private fun stmt() = when (kind()) {
        TokKind.PRINT -> printStmt()
        else -> expr().wrap()
    }

    private fun printStmt(): Stmt.Print {
        val tok = consume()
        val expr = expr()

        return Stmt.Print(expr, tok.loc)
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
            left = into(AnyBinOp(left, operator, right, Info.at(loc))).wrap()
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
            TokKind.NOT -> UnOp.Not(operand, Info.at(loc)).wrap()
            TokKind.MINUS -> UnOp.Neg(operand, Info.at(loc)).wrap()
            // this, by definition, shouldn't ever happen--check() restricts our possible types down to TokKind.MINUS
            // and TokKind.NOT.
            else -> Expr.Empty(here())
        }
    }

    private fun call(): Expr {
        return primary()
    }

    private fun primary(): Expr {
        val node =  when (kind()) {
            TokKind.LPAREN -> grouped()

            TokKind.TRUE -> Lit.BoolLit(true, consumeHere()).wrap()
            TokKind.FALSE -> Lit.BoolLit(false, consumeHere()).wrap()
            TokKind.NIL -> Lit.NilLit(consumeHere()).wrap()

            TokKind.INT -> intLit().wrap()
            TokKind.FLOAT -> floatLit().wrap()
            TokKind.LBRACKET -> listOrTable().wrap()
            TokKind.STR -> strLit().wrap()
            TokKind.INTERPOL -> interpol().wrap()

            // TokKind.ID -> consume().let { Expr.Access(it.lexeme, Info.at(it.loc)) }

            else -> Expr.empty().also { showMsg(ParseErr.expectedExpr(consume())) }
        }

        return exprMeta(node, to = Target.PRIMARY)
    }

    private fun intLit(): Lit.IntLit {
        return consume().let { Lit.IntLit(value = it.lexeme.toInt(), Info.at(it.loc)) }
    }

    private fun floatLit(): Lit.FloatLit {
        return consume().let { Lit.FloatLit(value = it.lexeme.toFloat(), Info.at(it.loc)) }
    }

    private fun strLit(): Lit.StrLit {
        return consume().let { Lit.StrLit(value = stringOf(it), Info.at(it.loc)) }
    }

    private fun grouped(): Expr.Parens {
        val from = consume().loc
        val expr = expr()
        val to = consume(TokKind.RPAREN)?.loc

        return Expr.Parens(expr, Info.at(from.tryMerge(with = to)))
    }

    private fun interpol(): Interpol {
        if (check(TokKind.STR)) {
            return Interpol.End(stringOf(curr()), consumeHere())
        }

        val begin = here().loc()
        val string = stringOf(consume())

        val expr = expr()
        consume(TokKind.INTERPOL_SEP)
        val next = interpol()

        val loc = begin.tryMerge(with = next.loc())
        return Interpol.Some(string, expr, next, Info.at(loc))
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

        return Lit.TableLit(emptyList(), emptyList(), Info.at(loc))
    }

    private fun table(leftBracket: Loc, firstKey: Expr): Lit.TableLit {
        val firstVal = expr()

        val keys = mutableListOf(firstKey)
        val vals = mutableListOf(firstVal)

        while (match(TokKind.COMMA)) {
            keys.add(expr())
            consume(TokKind.COL)
            vals.add(expr())
        }

        val rightBracket = consume(TokKind.RBRACKET)?.loc
        val loc = leftBracket.tryMerge(with = rightBracket)

        return Lit.TableLit(keys, vals, Info.at(loc))
    }

    private fun exprMeta(node: Expr, to: Target): Expr {
        return when (val parsed = parseAllMeta(node, to).reduce { a, b -> a + b }) {
            is MetaResult.Success -> node.update(
                node.info() + parsed.meta
            )

            is MetaResult.Fail -> node
        }
    }

    private fun parseAllMeta(node: Expr, to: Target): List<MetaResult> {
        val parsed = ParseMeta(ctx()).parse(to)
        if (parsed.isEmpty()) {
            return listOf(parsed)
        }

        if (parsed is MetaResult.Fail) {
            showMsg(ParseErr.metaFail(node.loc(), parsed.reason))
        }

        return listOf(parsed) + parseAllMeta(node, to)
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
            showMsg(ParseErr.expectedTok(here().loc(), kind))
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

    private fun hadErr(): Boolean {
        return state.hadErr
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

    private fun funCallWithoutParens(): Boolean {
        return kind().isExprStarter() && !hadNewline()
    }

    private fun ctx(): ParseCtx {
        return ParseCtx(window, typeTable, ctx)
    }

    // in Neve, we would have an additional restriction here:
    // fun string_of(tok StrTok)
    // with StrTok = Tok where self.kind in [TokKind.Str, TokKind.Interpol]
    private fun stringOf(tok: Tok): String {
        return tok.lexeme
    }

    private fun check(vararg kinds: TokKind): Boolean {
        return window.check(*kinds)
    }

    private fun match(vararg kinds: TokKind): Boolean {
        return window.match(*kinds)
    }

    private fun consumeHere(): Info {
        return here().also { consume() }
    }

    private fun hadNewline(): Boolean {
        return window.hadNewline()
    }

    private fun isAtEnd(): Boolean {
        return window.isAtEnd()
    }

    private fun curr(): Tok {
        return window.curr
    }

    private fun here(expr: Expr): Info {
        return Info(window.here(), expr.type(), Meta.empty())
    }

    private fun here(): Info {
        return Info.at(window.here())
    }

    private fun kind(): TokKind {
        return window.kind()
    }
}