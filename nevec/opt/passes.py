from nevec.ir.ir import *

from nevec.ast.visit import Visit

class Pass(Visit[Ir, None]):
    def __init__(self, syms: Syms):
        self.opts: List[Tac] = []

        self.syms: Syms = syms

        self.new_indices: Dict[int, int] = {}

    def optimize(self, ir: Block) -> Block:
        return self.optimize_tacs(ir.tacs, ir.label)

    def optimize_tacs(self, tacs: List[Tac], label: str) -> Block:
        if tacs == []:
            return Block(self.opts, label)

        head = tacs[0]
        self.visit(head)

        return self.optimize_tacs(tacs[1:], label)

    def emit(self, *tac: Tac):
        self.opts.extend(tac)

    def find_new_index(self, sym: Sym) -> Optional[int]:
        return next(
            (
                i 
                for i, o in enumerate(self.opts)
                if o.sym.full_name == sym.full_name
            ),
            None 
        )

    def elim_if_dead(self, sym: Sym):
        if sym.uses > 0:
            return

        sym.is_deleted = True
        index = self.find_new_index(sym)

        if index is None:
            raise ValueError(
                "attempt to eliminate symbol that does not exist: "
                f"{sym.full_name}"
            )

        del self.opts[index]

        del sym

    def visit(self, node: Ir, *ctx: Tac):
        method_name = "visit_" + type(node).__name__
        method = getattr(self, method_name, None)

        if method is None:
            # i.e. this optimization pass doesn't involve type(node)
            self.emit(*ctx)
            return

        method(node, *ctx) 

    def visit_Tac(self, tac: Tac):
        self.visit(tac.expr, tac)

    def is_propagatable(self, operand: Operand) -> bool:
        return (
            isinstance(operand.expr, IConst) and
            operand.sym.uses <= 1 
        )

