from typing import override

from nevec.ast.ast import Expr

from nevec.check.env import Env
from nevec.check.help import Inform, Suggest

from nevec.err.err import *
from nevec.err.report import Report

from nevec.lex.tok import Loc

class SimpleErr(Err):
    def __init__(self, msg: str, locus: Loc, note: str):
        self.msg: str = msg
        self.locus: Loc = locus
        self.note: str = note

        self.err: Err = self.make_err()

    @override
    def emit(self) -> str:
        return self.err.emit()

    def make_err(self) -> Err:
        return Report.err(
            self.msg,
            self.locus
        ).show(
            Line(self.locus).add(
                Inform.at(self.locus, that=self.note)
            )
        )
