from typing import Tuple

from nevec.ast.ast import Ast

from nevec.check.sem.sem import SemCheck
from nevec.check.type.type import TypeCheck

class Check:
    @staticmethod
    def err(ast: Ast) -> Tuple[Ast, bool]:
        sem = SemCheck()

        new_ast = sem.visit(ast)

        if sem.had_err:
            return ast, True

        # TODO: add analysis

        type = TypeCheck()

        return new_ast, type.visit(new_ast)
