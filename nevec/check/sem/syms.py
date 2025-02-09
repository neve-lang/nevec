from typing import List

from nevec.ast.ast import Access, Call
from nevec.ast.type import Type, Types

from nevec.check.env import *

type Callsite = Call | Access

class UnboundSym(Sym):
    def __init__(self, name: str):
        self.name: str = name
        self.type: Type = Types.UNRESOLVED

        self.callsites: List[Callsite] = []

    @staticmethod
    def from_sym(sym: Sym) -> "UnboundSym":
        if isinstance(sym, UnboundSym):
            return sym

        return UnboundSym(sym.name)

    def add(self, callsite: Optional[Callsite]):
        if callsite is None:
            return

        self.callsites.append(callsite)

    def __repr__(self) -> str:
        return f"{self.name} -> {self.type}"


class UnboundEnv:
    def __init__(self):
        self.syms: Dict[str, UnboundSym] = {}

    def add(self, sym: UnboundSym):
        self.syms[sym.name] = sym
        
    def get(self, name: str):
        found = self.syms.get(name, None)

        if found is None:
            unbound = UnboundSym(name)
            self.add(unbound)

            return unbound

        return UnboundSym.from_sym(found)

    def is_empty(self) -> bool:
        return len(self.syms) == 0

class Envs:
    def __init__(self):
        self.bound: Env = Env()
        self.unbound: UnboundEnv = UnboundEnv()

    def add(self, sym: Sym):
        self.bound.add(sym)

    def get_existing(self, name: str) -> Optional[Sym]:
        return self.bound.get(name)

    def get(
        self,
        name: str,
        callsite: Optional[Callsite]=None,
    ) -> Sym:
        found = self.bound.get(name)

        if found is not None:
            return found

        unbound = self.improvise(name)
        unbound.add(callsite)

        return unbound

    def existing(self) -> Env:
        return self.bound

    def improvise(self, name: str) -> UnboundSym:
        return self.unbound.get(name)
