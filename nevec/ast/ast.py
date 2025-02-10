from typing import Self, Optional, List

from nevec.ast.type import *
from nevec.lex.tok import Tok, TokType, Loc

from enum import auto, Enum

class Ast:
    def __init__(self, loc: Loc):
        self.loc = loc

    @staticmethod
    def empty() -> "Ast":
        return Ast(Loc.new())


class Program(Ast):
    def __init__(self, decls: List["Decl"]):
        self.decls = decls

    @staticmethod
    def empty() -> "Program":
        return Program([])


class Decl(Ast):
    def __init__(self, loc: Loc):
        self.loc: Loc = loc


class Consts(Decl):
    class Member:
        def __init__(
            self,
            name: str,
            type: Type,
            expr: "Expr",
            loc: Loc
        ):
            self.name: str = name
            self.type: Type = type
            self.expr: Expr = expr
            self.loc: Loc = loc


    def __init__(self, members: List[Member]):
        self.members: List[Consts.Member] = members


class Stmt(Decl):
    def __init__(self, loc: Loc):
        self.loc: Loc = loc


class Print(Stmt):
    def __init__(self, loc: Loc, expr: "Expr"):
        self.loc: Loc = loc
        self.expr: Expr = expr


class Expr(Stmt):
    def __init__(self, type: Type, loc: Loc):
        self.type = type
        self.loc = loc

    @staticmethod
    def empty() -> "Expr":
        return Expr(
            Types.UNKNOWN,
            Loc.new()
        )

    def infer_type(self) -> Type:
        return self.type


class Parens(Expr):
    def __init__(self, expr: Expr, loc: Loc):
        self.expr = expr
        self.loc = loc
        self.type = self.infer_type()

    def infer_type(self) -> Type:
        return self.expr.infer_type()


class Access(Expr):
    def __init__(self, tok: Tok, type: Type=Types.UNRESOLVED):
        self.name: str = tok.lexeme
        self.loc: Loc = tok.loc

        self.type: Type = type

    def infer_type(self) -> Type:
        return Types.UNRESOLVED


class AccessConst(Expr):
    def __init__(self, name: str, loc: Loc, type: Type=Types.UNRESOLVED):
        self.name: str = name
        self.loc: Loc = loc

        self.type: Type = type

    @staticmethod
    def from_access(access: Access) -> "AccessConst":
        return AccessConst(access.name, access.loc, access.type)

    def infer_type(self) -> Type:
        return Types.UNRESOLVED


class Call(Expr):
    # right now, we only support one argument, but still
    class Args:
        def __init__(self, exprs: List[Expr]):
            self.exprs: List[Expr] = exprs

    def __init__(self, callee: Expr, args: Args):
        self.callee: Expr = callee
        self.args: Call.Args = args

        self.type: Type = Types.UNRESOLVED

    def infer_type(self) -> Type:
        return Types.UNRESOLVED


class Op(Expr):
    ...


class UnOp(Op):
    class Op(Enum):
        NEG = auto()
        NOT = auto()


    def __init__(self, op: Op, expr: Expr, loc: Loc):
        self.op = op
        self.expr = expr
        self.loc = loc
        self.type = self.infer_type()

    def infer_type(self):
        return self.expr.infer_type()


class BinOp(Op):
    class Op(Enum):
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

        CONCAT = auto()


    def __init__(
        self, 
        left: Expr, 
        op: Op, 
        right: Expr, 
        tok: Tok, 
        loc: Loc,
    ):
        self.left = left
        self.op = op
        self.right = right
        self.tok = tok
        self.loc = loc

        self.type = self.infer_type()

    @staticmethod
    def from_tok(tok: Tok):
        return BinOp.Op(tok.type.value - TokType.PLUS.value + 1)

    def infer_type(self, base_type: Optional[Type]=None) -> Type:
        if self.left.type != self.right.type:
            return Types.UNKNOWN

        base_type = base_type if base_type else self.left.type 

        return base_type.unless_unknown(self.left.type, self.right.type)


class Bitwise(BinOp):
    def infer_type(self, base_type=Types.INT) -> Type:
        if (
            self.left.type != Types.INT or
            self.right.type != Types.INT
        ):
            return Types.UNKNOWN

        return super().infer_type(base_type)


class Comparison(BinOp):
    def infer_type(self, base_type=Types.BOOL) -> Type:
        return super().infer_type(base_type)


class Arith(BinOp):
    def infer_type(self, base_type=None):
        _ = base_type

        if self.left.type != self.right.type:
            return Types.UNKNOWN

        if not self.left.type.is_num():
            return Types.UNKNOWN

        return self.left.type.unless_unknown(self.left.type, self.right.type)


class Concat(BinOp):
    def infer_type(self, base_type=None):
        _ = base_type

        if self.left.type != self.right.type:
            return Types.UNKNOWN

        if not self.left.type.is_str():
            return Types.UNKNOWN

        return self.left.type.unless_unknown(self.left.type, self.right.type)


class Show(Expr):
    def __init__(self, expr: Expr, loc: Loc):
        self.expr: Expr = expr
        self.loc: Loc = loc

        self.type: Type = self.infer_type()
    
    def infer_type(self) -> Type:
        return Types.STR


class Table(Expr):
    def __init__(self, keys: List[Expr], vals: List[Expr], loc: Loc):
        self.keys: List[Expr] = keys
        self.vals: List[Expr] = vals
        self.loc: Loc = loc

        self.type: TableType = self.infer_type()

    @staticmethod
    def empty(loc: Loc) -> "Table":
        return Table(
            [],
            [],
            loc
        )

    def matches_first_key(self, key: Type) -> bool:
        first_key = self.keys[0].type

        return key == first_key

    def matches_first_val(self, val: Type) -> bool:
        first_val = self.vals[0].type

        return val == first_val

    def infer_type(self) -> TableType:
        type = TableType(Types.UNKNOWN, Types.UNKNOWN)

        if (
            len(self.keys) != len(self.vals) or
            self.keys == []
        ):
            return type

        first_key = self.keys[0]
        remaining_keys = self.keys[1:]

        if list(
            filter(lambda k: k.type != first_key.type, remaining_keys)
        ) == []:
            type.key = first_key.type

        first_val = self.vals[0]
        remaining_vals = self.vals[1:]

        if list(
            filter(lambda v: v.type != first_val.type, remaining_vals)
        ) == []:
            type.val = first_val.type

        return type


class Int(Expr):
    def __init__(self, value: int, loc: Loc):
        self.value = value
        self.loc = loc

        self.type = self.infer_type()

    def infer_type(self) -> Type:
        return Types.INT


class Float(Expr):
    def __init__(self, value: float, loc: Loc):
        self.value = value
        self.loc = loc

        self.type = self.infer_type()

    def infer_type(self) -> Type:
        return Types.FLOAT

    def __repr__(self):
        return str(self.value)
        

class Bool(Expr):
    def __init__(self, value: bool, loc: Loc):
        self.value = value
        self.loc = loc

        self.type = self.infer_type()

    def infer_type(self) -> Type:
        return Types.BOOL


class Str(Expr):
    def __init__(self, value: str, loc: Loc):
        self.value = value
        self.loc = loc

        self.type = self.infer_type()

    @staticmethod
    def empty():
        return Str("", Loc.new())

    @staticmethod
    def trim_quotes(value: str):
        begin = 1 if value[0] == "\"" else 0
        end = -1 if value[-1] == "\"" else len(value)

        return value[begin:end]
    
    def infer_type(self) -> Type:
        return (
            Types.STR
            if not self.is_unicode()
            else Types.STR8
        )

    def is_unicode(self) -> bool:
        encoded = self.value.encode("utf8")
        
        try:
            _ = encoded.decode("ascii")
            return False
        except UnicodeDecodeError:
            return True


class Interpol(Expr):
    def __init__(self, left: str, expr: Expr, next: Self | Str, loc: Loc):
        self.left = left
        self.expr = expr
        self.next = next
        self.loc = loc

        self.type = self.infer_type()

    def infer_type(self) -> Type:
        return Types.STR


class Nil(Expr):
    def __init__(self, loc: Loc):
        self.loc = loc

        self.type = self.infer_type()

    def infer_type(self) -> Type:
        return Types.NIL
