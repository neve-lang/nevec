from typing import Any

from nevec.ast.ast import Ast
from nevec.ast.visit import Visit

class Phase(Visit[Ast, bool]):
    def err(self) -> bool:
        return True

    def okay(self) -> bool:
        return False

    def check_for(self, *what: Ast) -> bool:
        if list(
            filter(lambda w: self.visit(w), what)
        ) != []:
            return self.err()

        return self.okay()

    def visit(self, node: Ast, *extra: Any) -> bool:
        method_name = "visit_" + type(node).__name__
        method = getattr(self, method_name, None)

        if method is None:
            # i.e. this checking phase doesn't involve type(node)
            return self.okay()

        return method(node, *extra) 
