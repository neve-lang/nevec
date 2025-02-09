from nevec.ast.ast import *

from nevec.check.errs.neverbound import NeverBoundErr
from nevec.check.phase import Phase
from nevec.check.sem.syms import *
from nevec.check.help import Assume, Fail

class SemCheck(Phase):
    def __init__(self):
        self.envs: Envs = Envs()

    def visit_Program(self, program: Program) -> bool:
        not_okay = self.check_for(*program.decls)

        unbound_syms = self.envs.unbound

        return Assume.that(unbound_syms.is_empty()).otherwise(
            *map(
                lambda s: NeverBoundErr(
                    s.name,
                    s.callsites[0].loc,
                    self.envs.bound
                ),
                unbound_syms.syms.values()
            )
        ) or not_okay

    def visit_Consts(self, consts: Consts) -> bool:
        def visit_Member(member: Consts.Member) -> bool:
            if self.visit(member.expr):
                return self.err()

            expr = member.expr

            member.type.update(to=expr.type) 

            sym = Sym(member.name, member.type)
            self.envs.add(sym)

            return self.okay()

        if self.err() in list(map(visit_Member, consts.members)):
            return self.err()

        return self.okay()

    def visit_Print(self, print: Print) -> bool:
        return self.visit(print.expr)

    def visit_Access(self, access: Access) -> bool:
        sym = self.envs.get_existing(access.name)
        
        if sym is not None:
            access.type.update(to=sym.type)
            return self.okay()

        return Fail.now(
            NeverBoundErr(
                access.name,
                access.loc,
                self.envs.existing()
            ) 
        )


    def visit_Call(self, call: Call) -> bool:
        exprs = call.args.exprs

        if self.check_for(*exprs):
            return self.err()

        return self.okay()
