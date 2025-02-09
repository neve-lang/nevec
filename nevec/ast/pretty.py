from typing import List, Tuple

from nevec.ast.ast import *
from nevec.ast.visit import Visit

class Pretty(Visit[Ast, str]):
    def __init__(self):
        self.indentation: int = 0

    def indent(self, s: str) -> str:
        self.indentation += 1 
        s = "  " * self.indentation + s
        self.indentation -= 1

        return s

    def visit_Program(self, program: Program) -> str:
        return "\n".join(map(self.visit, program.decls))

    def visit_Print(self, print: Print) -> str:
        return f"print {self.visit(print.expr)}"

    def visit_Consts(self, consts: Consts) -> str:
        def make_pairs(
            names: List[str],
            types: List[Type],
            exprs: List[Expr]
        ) -> List[Tuple[str, Type, Expr]]:
            if names == []:
                return []

            name = names[0]
            type = types[0]
            expr = exprs[0]

            return [(name, type, expr)] + make_pairs(
                names[1:],
                types[1:],
                exprs[1:]
            )
        names = [c.name for c in consts.members]
        types = [c.type for c in consts.members]
        exprs = [c.expr for c in consts.members]

        pairs = make_pairs(names, types, exprs)

        return (
            "const\n" +
            "\n".join(
                map(
                    lambda p: self.indent(
                        f"{p[0]} {p[1]} = {self.visit(p[2])}"
                    ),
                    pairs
                )
            ) +
            "end"
        )

    def visit_Parens(self, parens: Parens) -> str:
        return f"({self.visit(parens.expr)})"

    def visit_Access(self, access: Access) -> str:
        return access.name

    def visit_UnOp(self, un_op: UnOp) -> str:
        op = (
            "-"
            if un_op.op == un_op.Op.NEG
            else "not "
        )

        return f"{op}{un_op.expr}"

    def visit_BinOp(self, bin_op: BinOp) -> str:
        left = self.visit(bin_op.left)
        right = self.visit(bin_op.right)

        return f"{left} {bin_op.tok.lexeme} {right}"

    def visit_Bitwise(self, bitwise: Bitwise) -> str:
        return self.visit_BinOp(bitwise)

    def visit_Arith(self, arith: Arith) -> str:
        return self.visit_BinOp(arith)

    def visit_Comparison(self, comparison: Comparison) -> str:
        return self.visit_BinOp(comparison)

    def visit_Show(self, show: Show) -> str:
        return f"{self.visit(show.expr)}.show"

    def visit_Table(self, table: Table) -> str:
        def make_keys_and_vals(
            table: Table,
            keys: List[Expr],
            vals: List[Expr]
        ) -> List[str]:
            if keys == []:
                return []

            key = self.visit(keys[0])
            val = self.visit(vals[0])

            return [f"{key}: {val}"] + make_keys_and_vals(
                table,
                keys[1:],
                vals[1:]
            )

        if table.keys == []:
            return "[:]"

        keys_and_vals = make_keys_and_vals(table, table.keys, table.vals)

        return "".join([
            "[",
            ", ".join(keys_and_vals),
            "]"
        ])

    def visit_Int(self, i: Int) -> str:
        return str(int(i.value))

    def visit_Bool(self, b: Bool) -> str:
        return str(b.value).lower()

    def visit_Str(self, s: Str) -> str:
        return f"\"{s.value}\""

    def visit_Interpol(self, interpol: Interpol) -> str:
        return "".join(
            [
                "\"", 
                interpol.left,
                "#{", 
                self.visit(interpol.expr), 
                "}", 
                Str.trim_quotes(self.visit(interpol.next)), 
                "\""
            ]
        )

    def visit_Nil(self, nil: Nil) -> str:
        return "nil"
