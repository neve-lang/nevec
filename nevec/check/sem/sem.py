from nevec.ast.ast import Ast
from nevec.ast.visit import Visit

class SemCheck(Visit[Ast, bool]):
    def visit_Program(self, program: Program) -> bool:
