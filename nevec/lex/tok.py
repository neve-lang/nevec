from dataclasses import dataclass
from enum import Enum, auto
from typing import Optional, Self

class Loc:
    def __init__(
        self, 
        col: int, 
        line: int,
        length: int,
        true_col: Optional[int]=None,
        true_length: Optional[int]=None
    ):
        self.col: int = col
        self.line: int = line
        self.length: int = length

        self.true_col: int = true_col if true_col else col
        self.true_length: int = true_length if true_length else length

        self.on_multiple_lines: bool = False

    @staticmethod
    def new():
        return Loc(0, 1, 0)

    @staticmethod
    def in_between(a: "Loc", b: "Loc") -> "Loc":
        col = (
            (a.col + b.col) // 2
            if a.line == b.line
            else a.col + 1
        )

        true_col = (
            (a.true_col + b.true_col) // 2
            if a.line == b.line
            else a.true_col + 1
        )

        length = (
            b.col - a.col 
            if a.line == b.line 
            else 1
        )

        true_length = (
            b.true_col - a.true_col
            if a.line == b.line
            else 1
        )

        return Loc(
            col,
            a.line,
            length,
            true_col,
            true_length
        )

    @staticmethod
    def right_after(other: "Loc") -> "Loc":
        return Loc(
            other.col + other.length,
            other.line,
            1,
            other.true_col + other.true_length,
            1
        )

    def copy(self):
        return Loc(
            self.col, 
            self.line, 
            self.length,
            self.true_col,
            self.true_length
        )
    
    def advance(self, char_size=1):
        self.length += 1
        self.true_length += char_size

    def newline(self):
        self.col = 0
        self.line += 1

        self.true_col = 0

    def sync(self):
        self.col += self.length
        self.length = 0

        self.true_col += self.true_length
        self.true_length = 0

    # not using `Self` here because silly mypy thinks Loc ≠ Self@Loc
    def union_hull(self, other: "Loc") -> "Loc":
        if self.line != other.line:
            if self.on_multiple_lines:
                return self
            
            if other.on_multiple_lines:
                return other

            earliest_loc = self if self.line < other.line else other

            earliest_loc.on_multiple_lines = True

            return earliest_loc

        max_loc = self if self.col > other.col else other

        min_col = min(self.col, other.col)
        max_col = max(self.col, other.col)

        min_true_col = min(self.true_col, other.true_col)
        max_true_col = max(self.true_col, other.true_col)

        length = max_col - min_col + max_loc.length
        true_length = max_true_col - min_true_col + max_loc.true_length

        return Loc(min_col, self.line, length, min_true_col, true_length)

    def __eq__(self, other: Self):
        return (
            self.col == other.col and
            self.line == other.line and
            self.length == other.length
        )

    def __repr__(self):
        return f"{self.line}:{self.col}"


class TokType(Enum):
    SEMICOL = auto()
    COL = auto()
    COMMA = auto()
    DOT = auto()
    DOT_DOT = auto()
    
    PLUS = auto()
    MINUS = auto()  
    STAR = auto()
    SLASH = auto()
    
    SHL = auto()
    SHR = auto()
    BIT_AND = auto()
    BIT_XOR = auto()
    BIT_OR = auto()

    NEQ = auto()
    EQ = auto()
    GT = auto()
    GTE = auto()
    LT = auto()
    LTE = auto()

    ASSIGN = auto()

    EXCLAM = auto()
    QUESTION = auto()

    AND = auto()
    OR = auto()

    ELSE = auto()
    END = auto()
    FOR = auto()
    FUN = auto()
    IDEA = auto()
    IF = auto()
    LET = auto()
    PRINT = auto()
    RETURN = auto()
    UNION = auto()
    VAR = auto()
    WHILE = auto()

    RPAREN = auto()
    RBRACKET = auto()
    PIPE = auto()

    LPAREN = auto()
    LBRACKET = auto()

    ID = auto()
    STR = auto()
    INT = auto() 
    FLOAT = auto()
    INTERPOL = auto()

    DO = auto()
    FALSE = auto()
    MATCH = auto()
    NIL = auto()
    NOT = auto()
    SELF = auto()
    TRUE = auto()
    WITH = auto()

    INTERPOL_SEP = auto()

    NEWLINE = auto()
    ERR = auto()
    EOF = auto()

    @staticmethod
    def match(seq: str) -> Optional["TokType"]:
        return TokTypes.TOKS.get(seq) 

    @staticmethod
    def match_keyword(id: str) -> Optional["TokType"]:
        return TokTypes.KEYWORDS.get(id)

    @staticmethod
    def is_expr_starter(type: "TokType") -> bool:
        return (
            type.value >= TokType.LPAREN.value and 
            type.value <= TokType.WITH.value
        )

    @staticmethod
    def is_stmt_starter(type: "TokType") -> bool:
        return (
            type.value >= TokType.ELSE.value and
            type.value <= TokType.WHILE.value
        )

class TokTypes:
    KEYWORDS = {
        "and": TokType.AND,
        "bor": TokType.BIT_OR,
        "do": TokType.DO,
        "else": TokType.ELSE,
        "end": TokType.END,
        "false": TokType.FALSE,
        "for": TokType.FOR,
        "fun": TokType.FUN,
        "if": TokType.IF,
        "let": TokType.LET,
        "match": TokType.MATCH,
        "nil": TokType.NIL,
        "not": TokType.NOT,
        "or": TokType.OR,
        "print": TokType.PRINT,
        "return": TokType.RETURN,
        "self": TokType.SELF,
        "true": TokType.TRUE,
        "union": TokType.UNION,
        "var": TokType.VAR,
        "while": TokType.WHILE,
        "with": TokType.WITH
    }

    TOKS = {
        ";": TokType.SEMICOL,
        ":": TokType.COL,
        ",": TokType.COMMA,
        ".": TokType.DOT,
        "..": TokType.DOT_DOT,
        
        "-": TokType.MINUS,
        "+": TokType.PLUS,
        "*": TokType.STAR,
        "/": TokType.SLASH,
        
        "<<": TokType.SHL,
        ">>": TokType.SHR,
        "&": TokType.BIT_AND,
        "^": TokType.BIT_XOR,

        "!=": TokType.NEQ,
        "=": TokType.ASSIGN,
        "==": TokType.EQ,
        ">": TokType.GT,
        ">=": TokType.GTE,
        "<": TokType.LT,
        "<=": TokType.LTE,

        "!": TokType.EXCLAM,
        "?": TokType.QUESTION,

        "(": TokType.LPAREN,
        ")": TokType.RPAREN,
        "[": TokType.LBRACKET,
        "]": TokType.RBRACKET,
        "|": TokType.PIPE,

        "}": TokType.INTERPOL_SEP
    }

@dataclass
class Tok:
    type: TokType 
    lexeme: str
    loc: Loc
    value: Optional[any] = None

    @staticmethod
    def eof() -> "Tok":
        return Tok(TokType.EOF, "", Loc.new())
