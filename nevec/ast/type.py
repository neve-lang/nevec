from enum import auto, Enum
from dataclasses import dataclass
from typing import Dict, Self

class TypeKind(Enum):
    UNKNOWN = auto()
    UNKNOWN_SND = auto()

    UNRESOLVED = auto()

    INT = auto()
    FLOAT = auto()
    BOOL = auto()
    NIL = auto()

    STR = auto()
    STR8 = auto()
    STR16 = auto()
    STR32 = auto()

    TABLE = auto()


@dataclass
class Type:
    kind: TypeKind
    name: str
    is_mutable: bool = False

    def is_num(self) -> bool:
         return (
            self == Types.INT or
            self == Types.FLOAT
        )

    def is_str(self) -> bool:
        return (
            self == Types.STR or
            self == Types.STR8 or
            self == Types.STR16 or
            self == Types.STR32
        )

    def poison(self):
        self = Types.UNKNOWN

    def is_ignorable(self) -> bool:
        return self.kind == TypeKind.UNKNOWN_SND

    def is_poisoned(self) -> bool:
        return self.kind == TypeKind.UNKNOWN

    def is_invalid(self) -> bool:
        return self == Types.UNKNOWN or self == Types.UNRESOLVED

    def is_valid(self) -> bool:
        return self != Types.UNKNOWN and self != Types.UNRESOLVED

    def unless_unknown(self, *others: "Type") -> "Type":
        if (
            list(filter(Type.is_poisoned, others)) != []
        ):
            return Types.UNKNOWN_SND
        
        return self

    def __ne__(self, other: Self) -> bool:
        return (
            self.name != other.name and
            (not self.is_poisoned() or not self.is_ignorable())
        )

    def __eq__(self, other: Self) -> bool:
        return self.name == other.name
    
    def __repr__(self) -> str:
        return self.name


class TableType(Type):
    def __init__(self, key: Type, val: Type):
        self.kind: TypeKind = TypeKind.TABLE

        self.key: Type = key
        self.val: Type = val

        self.name = f"[{self.key}: {self.val}]"

        self.is_mutable = True

    def is_poisoned(self) -> bool:
        return self.key.is_poisoned() or self.val.is_poisoned()

    def is_valid(self) -> bool:
        return self.key.is_valid() and self.val.is_valid()


class Types:
    UNKNOWN = Type(TypeKind.UNKNOWN, "Unknown")
    UNKNOWN_SND = Type(TypeKind.UNKNOWN_SND, "Unknown")

    UNRESOLVED = Type(TypeKind.UNRESOLVED, "Unresolved")

    INT = Type(TypeKind.INT, "Int")
    FLOAT = Type(TypeKind.FLOAT, "Float")
    BOOL = Type(TypeKind.BOOL, "Bool")
    NIL = Type(TypeKind.NIL, "Nil")

    STR = Type(TypeKind.STR, "Str")
    STR8 = Type(TypeKind.STR8, "Str8")
    STR16 = Type(TypeKind.STR16, "Str16")
    STR32 = Type(TypeKind.STR32, "Str32")


class TypeTable:
    def __init__(self):
        self.types: Dict[str, Type] = {
            "Int": Types.INT,
            "Float": Types.FLOAT,
            "Bool": Types.BOOL,
            "Nil": Types.NIL,
            "Str": Types.STR
        }
        
    def register(self, type: Type):
        self.types[type.name] = type
