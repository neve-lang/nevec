from typing import Callable, Optional

from nevec.err.err import Err, Note, NoteType, Line, Suggestion
from nevec.err.report import Report
from nevec.lex.lex import Lex
from nevec.lex.tok import Loc, Tok, TokType, TokTypes

from nevec.ast.ast import *

class ParseErr:
    @staticmethod
    def unexpected_char(tok: Tok) -> Err:
        loc = tok.loc

        msg = tok.value if tok.value is not None else "invalid character"

        err = Report.err(
            msg,
            loc
        ).show(
            Line(
               loc 
            ).add(
                Note(
                    NoteType.ERR,
                    loc,
                    f"here"
                )
            )
        )

        return err

    @staticmethod
    def unexpected_tok(tok: Tok, expected: TokType) -> Err:
        loc = tok.loc
        
        expected_lexeme = {
            lexeme 
            for lexeme in TokTypes.TOKS
            if TokTypes.TOKS[lexeme] == expected
        }

        expected_lexeme = list(expected_lexeme)[0]

        err = Report.err(
            "unexpected token",
            loc
        ).show(
            Line(
               loc 
            ).add(
                Note(
                    NoteType.ERR,
                    loc,
                    f"expected '{expected_lexeme}' but found '{tok.lexeme}'"
                )
            )
        ).suggest(
            Suggestion(
                f"you can replace '{tok.lexeme}'",
                f"replaced '{tok.lexeme}' with '{expected_lexeme}'",
                loc,
                expected_lexeme
            )
        )

        return err

    @staticmethod
    def expected_tok(loc: Loc, expected: TokType) -> Err:
        expected_lexeme = [
            lexeme 
            for lexeme in TokTypes.TOKS
            if TokTypes.TOKS[lexeme] == expected
        ]

        expected_lexeme = expected_lexeme[0]

        err = Report.err(
            f"'{expected_lexeme}' was expected, but found nothing",
            loc
        ).show(
            Line(
                loc
            ).add(
                Note(
                    NoteType.ERR,
                    loc,
                    f"expected '{expected_lexeme}'"
                )
            )
        ).suggest(
            Suggestion(
                f"however, you can insert it",
                f"added '{expected_lexeme}'",
                loc,
                expected_lexeme
            )
        )

        return err

    @staticmethod
    def expected_expr(tok: Tok) -> Err:
        loc = tok.loc

        is_at_end = tok.type == TokType.EOF
        lexeme = tok.lexeme if not is_at_end else "end of file"

        err = Report.err(
            "expected an expression",
            loc
        ).show(
            Line(
                loc,
                header_msg=f"'{lexeme}' is not considered an expression",
                show_previous_line=is_at_end
            ).add(
                Note(
                    NoteType.ERR,
                    loc,
                    f"expected an expression, but found '{lexeme}'"
                    if not is_at_end else
                    "expected an expression, but found nothing" 
                )
            )
        )

        return err

    @staticmethod
    def expected(what: str, got: Tok) -> Err:
        loc = got.loc

        err = Report.err(
            f"expected {what}",
            loc
        ).show(
            Line(
                loc,
                header_msg=f"'{got.lexeme}' is not considered an expression"
            ).add(
                Note(
                    NoteType.ERR,
                    loc,
                    f"not {what}"
                )
            )
        )

        return err

class Parse:
    def __init__(self, code: str):
        self.lex: Lex = Lex(code)
        self.curr: Tok = Tok.eof()
        self.prev: Tok = Tok.eof()

        self.had_err: bool = False
        self.panic_mode: bool = False

        self.file_name = self.lex.file_name
        self.lines = self.lex.lines

        self.advance()

    def show_err(self, err: Err):
        if self.panic_mode:
            return

        self.panic_mode = True 
        self.had_err = True
        err.print()

    def advance(self):
        self.prev = self.curr

        while True:
            self.curr = self.lex.next()

            if self.curr.type == TokType.NEWLINE:
                self.prev = self.curr
                continue

            if self.curr.type != TokType.ERR:
                break

            # TODO: (re)implement proper error reporting
            self.show_err(ParseErr.unexpected_char(self.curr))

    def check(self, *type: TokType) -> bool:
        return self.curr.type in type
    
    def expect(self, type: TokType):
        if self.check(type):
            self.advance()
            return

        if self.check(TokType.EOF, TokType.NEWLINE):
            self.show_err(ParseErr.expected_tok(self.curr.loc, type))
            return

        self.show_err(ParseErr.unexpected_tok(self.curr, type))

    def match(self, *type: TokType) -> bool:
        if self.check(*type):
            self.advance()
            return True

        return False

    def had_newline(self) -> bool:
        return self.prev.type == TokType.NEWLINE

    def is_at_end(self) -> bool:
        return self.check(TokType.EOF)

    def consume(self) -> Tok:
        tok = self.curr
        self.advance()

        return tok

    def consume_expect(self, type: TokType) -> Optional[Tok]:
        if not self.check(type):
            self.expect(type)
            return None

        return self.consume()

    def parse(self) -> Ast:
        decls = []

        while not self.match(TokType.EOF):
            decls.append(self.decl())

        program = Program(decls)

        if self.had_err:
            del program
            return Ast.empty()
        
        return program

    def expr(self) -> Expr:
        return self.bit_or()

    def bit_or(self) -> Bitwise:
        return self.bin_op(Bitwise, self.bit_xor, TokType.BIT_OR)
    
    def bit_xor(self) -> Bitwise:
        return self.bin_op(Bitwise, self.bit_and, TokType.BIT_XOR)
    
    def bit_and(self) -> Bitwise:
        return self.bin_op(Bitwise, self.equality, TokType.BIT_AND)

    def equality(self) -> Comparison:
        return self.bin_op(
            Comparison,
            self.comparison,
            TokType.EQ,
            TokType.NEQ
        ) 

    def comparison(self) -> Comparison:
        return self.bin_op(
            Comparison,
            self.bit_shift,
            TokType.GT,
            TokType.GTE,
            TokType.LT,
            TokType.LTE
        )

    def bit_shift(self) -> Bitwise:
        return self.bin_op(Bitwise, self.term, TokType.SHL, TokType.SHR)

    def term(self) -> Arith:
        return self.bin_op(Arith, self.factor, TokType.PLUS, TokType.MINUS)

    def factor(self) -> Arith:
        return self.bin_op(Arith, self.unary, TokType.STAR, TokType.SLASH)
    
    def bin_op(self, type: type, fun: Callable, *ops: TokType) -> BinOp:
        left = fun()

        while self.check(*ops):
            op = self.consume()

            right = fun()

            loc = left.loc.union_hull(right.loc)
            left = type(
                left,
                BinOp.from_tok(op),
                right,
                op,
                loc
            )

        return left

    def unary(self) -> Expr:
        if not self.check(TokType.MINUS, TokType.NOT):
            return self.call()
        
        op = self.consume()
        operand = self.unary()

        unop_type = (
            UnOp.Op.NEG
            if op.type == TokType.MINUS
            else UnOp.Op.NOT
        )

        loc = op.loc.union_hull(operand.loc)
        return UnOp(unop_type, operand, loc)

    def call(self) -> Expr:
        expr = self.primary()

        while True:
            if self.match(TokType.LPAREN):
                expr = self.fun_call(expr, parens=True)
                continue

            if (
                TokType.is_expr_starter(self.curr.type) and
                not self.had_newline()
            ):
                expr = self.fun_call(expr)
                continue

            # this silly little check is just to accomodate for the special 
            # case of:
            # let msg = (
            #   "Hello, "
            #   "world!"
            # )
            if expr.type.is_str() and self.check(TokType.STR):
                expr = self.str_concat(expr)
                continue
            
            else:
                break

        return expr

    def fun_call(self, callee: Expr, parens=False) -> Expr:
        _ = parens

        if callee.type.is_str():
            return self.str_concat(callee)

        raise NotImplementedError("function calls not implemented yet")

    def str_concat(self, left: Expr) -> Expr:
        right = self.expr()         

        imaginary_loc = Loc.in_between(left.loc, right.loc)
        imaginary_tok = Tok(TokType.PLUS, " ", imaginary_loc)

        full_loc = left.loc.union_hull(right.loc)

        return Concat(
            left,
            BinOp.Op.CONCAT,
            right,

            imaginary_tok,
            full_loc
        )

    def primary(self) -> Expr:
        tok = self.curr

        match tok.type:
            case TokType.INT:
                return self.int_lit()

            case TokType.FLOAT:
                return self.float_lit()

            case TokType.TRUE | TokType.FALSE:
                self.advance()
                return Bool(tok.type == TokType.TRUE, tok.loc)
            
            case TokType.NIL:
                self.advance()
                return Nil(tok.loc)

            case TokType.LPAREN:
                return self.grouping()

            case TokType.LBRACKET:
                return self.list_or_table()

            case TokType.STR:
                return self.str_lit()
            
            case TokType.INTERPOL:
                return self.interpol()

        self.show_err(ParseErr.expected_expr(tok))
        return Expr(Types.UNKNOWN, tok.loc)

    def int_lit(self) -> Int:
        # TODO: allow hexadecimal, binary, and octal integers
        # and implement bounds checking for integers (LONG_MIN, LONG_MAX)
        tok = self.consume()

        value = int(tok.lexeme)

        return Int(value, tok.loc)

    def float_lit(self) -> Float:
        tok = self.consume()

        value = float(tok.lexeme)

        return Float(value, tok.loc)
    
    def str_lit(self) -> Str:
        tok = self.consume()

        value = tok.lexeme
        raw_str = Str.trim_quotes(value)

        return Str(raw_str, tok.loc)

    def interpol(self) -> Interpol:
        tok = self.consume() 

        value = tok.lexeme
        raw_str = Str.trim_quotes(value)

        interpol_expr = self.expr()

        self.expect(TokType.INTERPOL_SEP)
        
        # TODO: make sure the interpol_expr implements Show.

        next = None
        if self.check(TokType.INTERPOL):
            next = self.interpol() 
        else:
            if not self.check(TokType.STR):
                self.show_err(ParseErr.expected("a string", got=self.curr))

                loc = tok.loc.union_hull(self.curr.loc)
                return Interpol(raw_str, interpol_expr, Str.empty(), loc)

            next = self.str_lit()

        loc = tok.loc.union_hull(next.loc)
        return Interpol(raw_str, interpol_expr, next, loc)

    def grouping(self) -> Parens:
        left_paren = self.consume().loc

        grouped = self.expr()
        
        right_paren = self.consume_expect(TokType.RPAREN)

        loc_end = right_paren.loc if right_paren is not None else grouped.loc

        loc = left_paren.union_hull(loc_end)
        return Parens(grouped, loc)

    def list_or_table(self) -> Expr:
        left_bracket = self.consume().loc

        if self.match(TokType.COL):
            return self.empty_table(left_bracket)

        first_expr = self.expr()

        if self.match(TokType.COL):
            return self.table(left_bracket, first_expr)

        raise NotImplementedError("lists not implemented yet")

    def table(self, left_bracket: Loc, first_key: Expr) -> Table:
        first_val = self.expr()

        keys = [first_key]
        vals = [first_val]

        while self.match(TokType.COMMA):
            key = self.expr()
            keys.append(key)

            self.consume_expect(TokType.COL)

            val = self.expr()
            vals.append(val)

        right_bracket = self.consume_expect(TokType.RBRACKET)

        loc_end = (
            right_bracket.loc 
            if right_bracket is not None 
            else self.prev.loc
        )

        loc = left_bracket.union_hull(loc_end)

        return Table(keys, vals, loc)
    
    def empty_table(self, left_bracket: Loc) -> Table:
        right_bracket = self.consume_expect(TokType.RBRACKET)
        
        loc_end = (
            right_bracket.loc 
            if right_bracket is not None 
            else self.prev.loc
        )

        loc = left_bracket.union_hull(loc_end)

        return Table.empty(loc)
