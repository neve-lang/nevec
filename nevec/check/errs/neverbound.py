from typing import override

from nevec.check.env import Env
from nevec.check.help import Suggest

from nevec.err.err import *
from nevec.err.report import Report

from nevec.lex.tok import Loc

class NeverBoundErr(Err):
    def __init__(self, name: str, locus: Loc, syms: Env):
        self.name: str = name
        self.locus: Loc = locus

        self.err = self.make_err(syms)

    @override
    def emit(self) -> str:
        return self.err.emit()

    def make_err(self, syms: Env) -> Err:
        err = Report.err(
            f"{self.name} is never defined",
            self.locus
        ).show(
            Line(self.locus).add(
                Note.err(
                    self.locus,
                    f"unknown symbol ‘{self.name}’"
                )
            )
        )

        suggestion = Suggest.similar_name_for(
            self.name,
            self.locus,
            based_on=syms,
        )

        if suggestion is None:
            return err

        return err.suggest(suggestion)

