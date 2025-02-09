from typing import Dict, Optional

from nevec.ast.type import Type

class Sym:
    def __init__(self, name: str, type: Type):
        self.name: str = name
        self.type: Type = type

    def __repr__(self) -> str:
        return f"{self.name} -> {self.type}"


class Env:
    def __init__(self):
        self.syms: Dict[str, Sym] = {}

    def add(self, sym: Sym):
        self.syms[sym.name] = sym

    def get(self, name: str) -> Optional[Sym]:
        return self.syms.get(name, None) 

    def is_empty(self) -> bool:
        return len(self.syms) == 0
