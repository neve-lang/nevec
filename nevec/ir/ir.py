from typing import List

from enum import auto, Enum

from nevec.ast.type import Type, Types

from nevec.ir.sym import *

from nevec.opcode.const import *
from nevec.opcode.opcode import Opcode

from nevec.lex.tok import Loc

class Block:
    def __init__(self, tacs: List["Tac"], label: str):
        self.tacs: List[Tac] = tacs
        self.label: str = label

    def format(self, tac: "Tac") -> str:
        return "\n   " + str(tac)
        
    def __eq__(self, other: Self) -> bool:
        return self.tacs == other.tacs

    def __repr__(self) -> str:
        return "".join([self.label, ":", *map(self.format, self.tacs)])


type Ir = IExpr | IOp | Tac | Block


class IOp:
    def __init__(self, sym: Sym, loc: Loc):
        self.sym: Sym = sym
        self.loc: Loc = loc


class IRet(IOp):
    def __repr__(self) -> str:
        return f"ret {self.sym}"


class IPrint(IOp):
    def __repr__(self) -> str:
        return f"print {self.sym}"


class ISpill(IOp):
    def __init__(self, sym: Sym, loc: Loc):
        self.sym: Sym = sym
        self.loc: Loc = loc

    def __repr__(self) -> str:
        return f"spill {self.sym}"


class IExpr:
    def __init__(self, type: Type, loc: Loc):
        self.type: Type = type
        self.loc: Loc = loc


class SetIExpr(IExpr):
    def __init__(self, type: Type, loc: Loc):
        self.type: Type = type
        self.loc: Loc = loc


class Operand:
    def __init__(
        self, 
        sym: Sym,
        expr: IExpr,
        loc: Loc
    ):
        self.sym: Sym = sym
        self.expr: IExpr = expr
        self.loc: Loc = loc

        self.type: Type = expr.type

    def __repr__(self) -> str:
        return str(self.sym)


class Tac:
    def __init__(
        self,
        sym: Sym,
        expr: IExpr | IOp,
        loc: Loc,
    ):
        self.sym: Sym = sym
        self.expr: IExpr | IOp = expr
        self.loc: Loc = loc

        # we can be 100% sure that this moment is the next moment
        # thanks to SSA
        self.moment: Moment = self.sym.first

        self.own_operand: Optional[Operand] = None

    def next_moment(self) -> Moment:
        return self.moment + 1

    def operand(self) -> Operand:
        assert isinstance(self.expr, IExpr)

        operand = Operand(
            self.sym,
            self.expr,
            self.loc
        )

        self.own_operand = operand
        return operand

    def update(self, other: Self):
        operand = other.operand()
        expr = operand.expr

        self.expr = expr

        if self.own_operand is None:
            return

        self.own_operand.expr = expr
    
    def __repr__(self) -> str:
        if isinstance(self.expr, IOp | SetIExpr):
            return str(self.expr)

        return f"{self.sym} = {self.expr}"


class IUnOp(IExpr):
    class Op(Enum):
        NEG = auto() 
        NOT = auto()
        IS_NIL = auto()
        IS_NOT_NIL = auto()
        IS_ZERO = auto()
        SHOW = auto()

        def opcode(self) -> Opcode:
            return Opcode(Opcode.NEG.value + self.value - 1)


    def __init__(
        self,
        op: Op,
        operand: Operand,
        loc: Loc,
        type: Type,
    ):
        self.op: IUnOp.Op = op
        self.operand: Operand = operand

        self.loc: Loc = loc
        self.type: Type = type

    def __repr__(self) -> str:
        match self.op:
            case IUnOp.Op.NEG:
                return f"neg {self.operand}"

            case IUnOp.Op.NOT:
                return f"not {self.operand}"

            case IUnOp.Op.IS_NIL:
                return f"isnil {self.operand}"

            case IUnOp.Op.IS_NOT_NIL:
                return f"isnotnil {self.operand}"
            
            case IUnOp.Op.IS_ZERO:
                return f"isz {self.operand}"

            case IUnOp.Op.SHOW:
                return f"show {self.operand}"


class IBinOp(IExpr):
    class Op(Enum):
        ADD = auto()
        SUB = auto()  
        MUL = auto()
        DIV = auto()

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

        def opcode(self) -> Opcode:
            return Opcode(Opcode.ADD.value + self.value - 1)


    def __init__(
        self,
        left: Operand,
        op: Op,
        right: Operand,
        op_lexeme: str,
        loc: Loc,
        type: Type,
    ):
        self.left: Operand = left
        self.op: IBinOp.Op = op
        self.right: Operand = right
        self.op_lexeme: str = op_lexeme

        self.loc: Loc = loc
        self.type = type

    def __repr__(self) -> str:
        return f"{self.left} {self.op_lexeme} {self.right}"


class IConcat(IExpr):
    def __init__(
        self,
        left: Operand,
        right: Operand,
        loc: Loc,
        type: Type,
    ):
        self.left: Operand = left
        self.right: Operand = right

        self.loc: Loc = loc
        self.type = type

    def op(self) -> Opcode:
        assert self.left.type == self.right.type

        return Opcode.CONCAT if self.left.type == Types.STR else Opcode.UCONCAT

    def __repr__(self) -> str:
        return f"{self.left} concat {self.right}"


class TableSet(SetIExpr):
    def __init__(
        self,
        table: Operand,
        key: Operand,
        expr: Operand,
        type: Type,
        loc: Loc
    ):
        self.table: Operand = table
        self.key: Operand = key

        self.expr: Operand = expr

        self.type: Type = type
        self.loc: Loc = loc

    def __repr__(self) -> str:
        return f"{self.table}[{self.key}] = {self.expr}"


class TableGet(IExpr):
    def __init__(
        self,
        table: Operand,
        key: Operand,
        loc: Loc,
        type: Type
    ):
        self.table: Operand = table
        self.key: Operand = key

        self.loc: Loc = loc
        self.type: Type = type

    def __repr__(self) -> str:
        return f"{self.table}[{self.key}]"


class IConst[T](IExpr):
    def __init__(self, value: T, loc: Loc, type: Type):
        self.value: T = value
        self.loc: Loc = loc
        self.type: Type = type

    def const(self) -> Const:
        raise NotImplementedError("emission not implemented")

    def is_identity(self) -> bool:
        return False


class IInt(IConst):
    def __init__(
        self,
        value: int,
        loc: Loc,
        type: Type,
    ):
        self.value: int = value

        self.loc: Loc = loc
        self.type: Type = type

    def const(self) -> Const:
        return Num(self.value)

    def is_identity(self) -> bool:
        return self.value == 0

    def __repr__(self) -> str:
        return f"{self.value}"


class IFloat(IConst):
    def __init__(
        self,
        value: float,
        loc: Loc,
        type: Type,
    ):
        self.value: float = value

        self.loc: Loc = loc
        self.type: Type = type

    def const(self) -> Const:
        return Num(self.value)

    def is_identity(self) -> bool:
        return self.value == 0.0

    def __repr__(self) -> str:
        return f"{self.value}"


class IBool(IConst):
    def __init__(
        self,
        value: bool,
        loc: Loc,
    ):
        self.value: bool = value

        self.loc: Loc = loc
        self.type: Type = Types.BOOL

    def const(self) -> Const:
        return BoolLit(self.value)

    def is_identity(self) -> bool:
        return True

    def __repr__(self) -> str:
        return str(self.value).lower()


class IStr(IConst):
    def __init__(
        self,
        value: str,
        loc: Loc,
        type: Type,
    ):
        self.value: str = value
        self.is_interned: bool = type == Types.STR or type == Types.STR8

        self.loc: Loc = loc
        self.type: Type = type

    def const(self) -> Const:
        encoding = self.encoding()
        value = (encoding, self.value, self.is_interned)

        return StrLit(value)

    def is_identity(self) -> bool:
        return self.value == ""

    def encoding(self) -> str:
        match self.type:
            case Types.STR:
                return "ascii"

            case Types.STR8:
                return "utf8"

            case Types.STR16:
                return "utf16"

            case Types.STR32:
                return "utf32"
            
            case _:
                raise ValueError("malformed IR")

    def __repr__(self) -> str:
        return f"\"{self.value}\""


class ITable(IConst):
    def __init__(
        self,
        keys: List[IConst],
        vals: List[IConst],
        loc: Loc,
        type: Type
    ):
        self.keys = keys
        self.vals = vals

        self.const_keys = [k.const() for k in self.keys]

        self.loc = loc
        self.type = type

    @staticmethod
    def empty(loc: Loc, type: Type) -> "ITable":
        return ITable(
            [],
            [],
            loc,
            type
        )

    def add_entry(self, key: IConst, val: IConst):
        existing = next(
            (
                i
                for i, k in enumerate(self.keys)
                if k.const() == key.const()
            ),
            None
        )

        if existing is not None:
            del self.keys[existing] 
            del self.vals[existing]

        self.keys.append(key)
        self.vals.append(val)

        self.const_keys.append(key.const())

    def const(self) -> Const:
        keys = [k.const() for k in self.keys]
        vals = [v.const() for v in self.vals]

        return TableLit(
            TableLit.make_entries(keys, vals)
        )

    def is_identity(self) -> bool:
        return len(self.keys) == 0

    def repr_keys_and_vals(
        self,
        keys: List[IConst],
        vals: List[IConst]
    ) -> List[str]:
        if keys == []:
            return []

        key = keys[0]
        val = vals[0]

        return [f"{key}: {val}"] + self.repr_keys_and_vals(
            keys[1:],
            vals[1:]
        )

    def __repr__(self) -> str:
        if self.keys == []:
            return "[:]"

        keys_and_vals = self.repr_keys_and_vals(self.keys, self.vals)

        return "".join([
            "[",
            ", ".join(keys_and_vals),
            "]"
        ])


class INil(IConst):
    def __init__(
        self,
        loc: Loc,
    ):
        self.loc: Loc = loc
        self.type: Type = Types.NIL

    def const(self) -> Const:
        return NilLit(None)

    def is_identity(self) -> bool:
        return True

    def __repr__(self) -> str:
        return "nil"

