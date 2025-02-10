from typing import List, Dict, Optional, Self, Tuple

type Moment = int

class Lifetime:
    def __init__(self, first: Moment, last: Optional[Moment]=None):
        self.first: Moment = first
        self.last: Optional[Moment] = last

    def intersects_with(self, other: Self) -> bool:
        assert self.last is not None and other.last is not None

        return (
            other.first < self.last and other.last > self.first
        )
    
    def is_valid_in(self, moment: Moment) -> bool:
        assert self.last is not None

        return self.first >= moment and self.last <= moment

    def __repr__(self) -> str:
        return f"({self.first}, {self.last})"


class Sym[T]:
    def __init__(
        self,
        name: str,
        index: int,
        moment: Moment,
        value: Optional[T]=None
    ):
        self.name: str = name
        self.index: int = index
        self.first: Moment = moment

        self.value: Optional[T] = value

        self.full_name = self.name + str(self.index)
        self.uses: int = 0

        self.is_deleted: bool = False

        self.lifetime: Optional[Lifetime] = None

    def rename(self, after: Self, assume_next=True):
        self.index = after.index + int(assume_next)
        self.full_name = self.name + str(self.index)

    def propagate(self):
        self.uses -= 1

    def last_used(self, last: Moment):
        self.uses += 1

        self.lifetime = Lifetime(self.first, last)

    def is_alive_in(self, moment: Moment) -> bool:
        assert self.lifetime is not None

        return self.lifetime.is_valid_in(moment)

    def copy(self) -> "Sym":
        return Sym(
            self.name,
            self.index,
            self.first
        )

    def __repr__(self) -> str:
        return self.full_name


class Syms:
    def __init__(self):
        self.syms: Dict[str, Sym] = {}

    def new_sym[T](
        self,
        moment: Moment,
        name: str="t",
        value: Optional[T]=None
    ) -> Sym:
        name, index = self.next_available_name(name)

        sym = Sym(name, index, moment, value)

        self.syms[sym.full_name] = sym

        return sym

    def next_available_name(self, name: str, index: int=0) -> Tuple[str, int]:
        full_name = name + str(index)

        if full_name not in self.syms.keys():
            return name, index

        return self.next_available_name(name, index + 1)

    def next_after(self, sym: Sym) -> Optional[Sym]:
        index = sym.index + 1

        next_name = f"{sym.name}{index}"

        return self.syms.get(next_name)

    def rename(self, sym: Sym, to: str):
        model = self.new_sym(sym.first, name=to, value=sym.value)

        sym.rename(after=model, assume_next=False)

    def cleanup(self):
        def give_new_name(syms: Dict[str, Sym], current: Sym) -> Sym:
            found = next(
                (
                    s 
                    for s in reversed(syms.values())
                    if s.name == current.name
                ),
                Sym(current.name, -1, current.first)
            )

            current.rename(after=found)
            
            return current

        def add_to(syms: Dict[str, Sym], sym: Sym):
            if sym.lifetime is None:
                sym.last_used(sym.first)

            new_sym = give_new_name(syms, sym)
            syms[new_sym.full_name] = new_sym

        self.syms = {n: s for n, s in self.syms.items() if not s.is_deleted}

        new_syms = {}

        list(map(lambda s: add_to(new_syms, s), self.syms.values()))

        self.syms = new_syms

    def values(self) -> List[Sym]:
        return list(self.syms.values())
