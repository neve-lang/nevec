from nevec.ast.ast import *
from nevec.check.phase import Phase

class SemCheck(Phase):
    def visit_Program(self, program: Program) -> bool:
        return self.check_for(*program.decls)
