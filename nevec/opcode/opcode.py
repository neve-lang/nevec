from enum import auto, Enum

from nevec.opcode.emit import Emit

class Opcode(Enum):
    PUSH = auto()
    PUSHLONG = auto()

    TRUE = auto()
    FALSE = auto()
    NIL = auto()

    ZERO = auto()
    ONE = auto()
    MINUSONE = auto()

    NEG = auto()
    NOT = auto()
    ISNIL = auto()
    ISNOTNIL = auto()
    ISZ = auto()
    SHOW = auto()

    ADD = auto()
    SUB = auto()
    MUL = auto()
    DIV = auto()
    SHL = auto()
    SHR = auto()
    BAND = auto()
    XOR = auto()
    BOR = auto()
    NEQ = auto()
    EQ = auto()
    GT = auto()
    LT = auto()
    GTE = auto()
    LTE = auto()

    CONCAT = auto()
    UCONCAT = auto()

    TABLENEW = auto()
    TABLESET = auto()
    TABLEGET = auto()

    PRINT = auto()
    RET = auto()
    
    def raw(self) -> int:
        return self.value - 1

    def emit(self) -> bytes:
        return Emit.integer(self.raw(), size=1)[0]
