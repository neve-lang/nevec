from nevec.ir.ir import *

from nevec.opt.passes import Pass
from nevec.opt.const import ConstFold
from nevec.opt.table import TablePropagation

class Opt:
    UNCONDITIONAL_PASSES: List[type[Pass]] = [
        TablePropagation
    ]

    PASSES: List[type[Pass]] = [
        ConstFold
    ]

    def __init__(self, syms: Syms, do_opt: bool):
        self.syms: Syms = syms
        
        self.all_passes: List[type[Pass]] = Opt.UNCONDITIONAL_PASSES

        if do_opt:
            self.all_passes.extend(Opt.PASSES)

    def optimize(self, ir: List[Block]) -> List[Block]:
        optimized = self.optimization_cycle(ir, self.all_passes)

        if optimized == ir:
            return optimized

        return self.optimize(optimized)

    def optimization_cycle(
        self,
        blocks: List[Block],
        passes: List[type[Pass]]
    ) -> List[Block]:
        if blocks == []:
            self.syms.cleanup()
            return []

        head = blocks[0]
        opt_block = self.optimize_block(head, passes)

        return [opt_block] + self.optimization_cycle(blocks[1:], passes)

    def optimize_block(self, block: Block, passes: List[type[Pass]]) -> Block:
        if passes == []:
            return block

        opt_pass = passes[0](self.syms)

        opt_ir = opt_pass.optimize(block)
        return self.optimize_block(opt_ir, passes[1:])
        
