from nevec.ast.ast import Ast
from nevec.check.sem.sem import SemCheck
from nevec.check.type.type import TypeCheck

class Check:
    @staticmethod
    def err(ast: Ast) -> bool:
        sem = SemCheck()

        if sem.visit(ast):
            return True

        # TODO: add analysis

        type = TypeCheck()

        if type.visit(ast):
            return True

        return False
