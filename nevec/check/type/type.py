from nevec.ast.ast import *

from nevec.check.phase import Phase

from nevec.check.errs import *
from nevec.check.help import *

from nevec.err.err import Err

class TypeCheck(Phase):
    def any_fail(self, parent: Expr, *what: Expr) -> bool:
        if (
            list(
                filter(lambda w: self.visit(w) or w.type.is_ignorable(), what)
            ) != [] or parent.type.is_ignorable()
        ):
            parent.type.poison()
            return self.err()

        return self.okay()

    def fail(self, err: Err) -> bool:
        err.print()
        return self.err()

    def visit_Program(self, program: Program) -> bool:
        return self.err() in list(map(self.visit, program.decls))

    def visit_Print(self, print: Print) -> bool:
        return self.visit(print.expr)

    def visit_Parens(self, parens: Parens) -> bool: 
        return self.visit(parens.expr)

    def visit_UnOp(self, un_op: UnOp) -> bool:
        expr = un_op.expr

        if self.any_fail(un_op, expr):
            return self.err()

        if un_op.op == UnOp.Op.NEG:
            return Assume(un_op, that=Type.is_num).or_fail(
                saying="can only negate Float or Int values"
            )
            
        if un_op.op == UnOp.Op.NOT:
            return Assume(un_op, Types.BOOL).or_fail(
                saying="can only flip booleans"
            )

        raise TypeError(
            f"unary op {un_op.op.name()} not implemented in check.py"
        )
        
    def visit_Bitwise(self, bitwise: Bitwise) -> bool:
        left = bitwise.left
        right = bitwise.right

        if self.any_fail(bitwise, left, right):
            return self.err()

        if bitwise.type.is_invalid(): 
            Assume.same_type(left, right).or_fail()
            
            Assume.both(left, right).are(Types.INT).or_fail(
                Inform.at(bitwise.tok.loc, "only accepts Int"),
                *Suggest.possible_conversions(Types.INT, left, right),
                saying="operands of bitwise operation must be Int"
            )

            return self.err()

        return self.okay()

    def visit_Comparison(self, comparison: Comparison):
        left = comparison.left
        right = comparison.right

        if self.any_fail(comparison, left, right):
            return self.err()

        return Assume(comparison, that=Type.is_valid).or_fail()

    def visit_Arith(self, arith: Arith):
        left = arith.left
        right = arith.right

        if self.any_fail(arith, left, right):
            return self.err()

        if arith.type.is_valid():
            return self.okay()

        Assume.same_type(left, right).or_fail(
            *Suggest.possible_conversions(Types.INT, left, right)
        )

        op = arith.tok

        Assume.both(left, right).are(Type.is_num).or_fail(
            Inform.at(op.loc, that="only accepts matching Int or Float"),
            *Suggest.possible_conversions(Types.INT, left, right),
            saying=(
                "operands of arithmetic operation must be "
                "either Int or Float"
            )
        )

        return self.err()

    def visit_Concat(self, concat: Concat) -> bool:
        left = concat.left
        right = concat.right

        if self.any_fail(concat, left, right):
            return self.err()

        if concat.type.is_valid():
            return self.okay()

        # right is always the culprit, as for the parser to produce a 
        # Concat node, it must find a Str node on the left hand side
        culprit = right

        Assume(culprit, that=Type.is_str).or_fail(
            Suggest.conversion_for(culprit, to=Types.STR)
        )

        Assume.same_type(culprit, left).or_fail(
            Suggest.conversion_for(culprit, to=left.type)
        )

        return self.err()

    def visit_Table(self, table: Table) -> bool:
        keys = table.keys
        vals = table.vals

        if self.any_fail(table, *keys, *vals):
            return self.err()

        if table.type.is_valid():
            return self.okay()

        if table.keys == []:
            return self.fail(TypeErr(
                "could not infer table's type",
                table.loc,
                table
            ))

        not_ok_keys = list(filter(
            lambda k: not table.matches_first_key(k.type),
            table.keys
        ))

        not_ok_vals = list(filter(
            lambda v: not table.matches_first_val(v.type),
            table.vals
        ))

        first_key = table.keys[0]
        first_val = table.vals[0]

        key_type = table.type.key
        val_type = table.type.val

        Assume.that(key_type.is_valid()).with_nodes(*not_ok_keys).with_types(
            first_key.type
        ).or_fail(
            Inform.type_of(first_key, saying="first key")  
        )

        Assume.that(val_type.is_valid()).with_nodes(*not_ok_vals).with_types(
            first_val.type
        ).or_fail(
            Inform.type_of(first_val, saying="first val")
        )

        return self.err()

    def visit_Interpol(self, interpol: Interpol) -> bool:
        if self.any_fail(interpol, interpol.expr, interpol.next):
            return self.err()

        # TODO: check if each expression implements Show
        return self.okay()
