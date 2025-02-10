from typing import Any

from nevec.ast.ast import *
from nevec.ast.visit import Visit

from nevec.check.errs.neverbound import NeverBoundErr
from nevec.check.errs.simple import SimpleErr
from nevec.check.sem.syms import *
from nevec.check.help import Assume, Fail

# using Any here because mypy just won’t stop complaining
# when the return type is so obvious
class SemCheck(Visit[Ast, Any]):
    def __init__(self):
        self.had_err: bool = False

        self.envs: Envs = Envs()

    def fail_with(self, node: Ast) -> Ast:
        self.had_err = True

        return node

    def consider(self, result: bool):
        self.had_err = result

    def to_concat(self, callee: Expr, call: Call) -> Concat:
        left = callee

        exprs = list(map(self.visit, call.args.exprs))
        first = exprs[0]

        self.consider(
            Assume.that(len(exprs) == 1).otherwise(
                SimpleErr(
                    "cannot call a Str type with multiple arguments",
                    first.loc,
                    "can only have one argument"
                )
            )
        )

        right = first

        imaginary_loc = Loc.in_between(left.loc, right.loc)
        imaginary_tok = Tok(TokType.PLUS, " ", imaginary_loc)

        full_loc = left.loc.union_hull(right.loc)

        return Concat(
            left,
            BinOp.Op.CONCAT,
            right,

            imaginary_tok,
            full_loc
        )

    def visit_Program(self, program: Program) -> Program:
        decls = list(map(self.visit, program.decls))

        unbound_syms = self.envs.unbound

        self.had_err = Assume.that(unbound_syms.is_empty()).otherwise(
            *map(
                lambda s: NeverBoundErr(
                    s.name,
                    s.callsites[0].loc,
                    self.envs.bound
                ),
                unbound_syms.syms.values()
            )
        ) or self.had_err

        return Program(decls)

    def visit_Consts(self, consts: Consts) -> Consts:
        def visit_Member(member: Consts.Member) -> Consts.Member:
            expr = self.visit(member.expr)

            member.type.update(to=expr.type) 

            sym = Sym(member.name, member.type)
            self.envs.add(sym)

            return member

        members = list(map(visit_Member, consts.members))
        return Consts(members)

    def visit_Print(self, print: Print) -> Print:
        return Print(print.loc, self.visit(print.expr))

    def visit_Access(self, access: Access) -> Access | AccessConst:
        sym = self.envs.get_existing(access.name)
        
        if sym is not None:
            new_type = sym.type

            return AccessConst(access.name, access.loc, new_type)

        self.had_err = Fail.now(
            NeverBoundErr(
                access.name,
                access.loc,
                based_on=self.envs.existing()
            ) 
        )

        return access

    def visit_Call(self, call: Call) -> Call | Concat:
        callee = self.visit(call.callee)

        if callee.type.is_str():
            return self.to_concat(callee, call)

        return call

    def visit_Str(self, s: Str) -> Str:
        return s
