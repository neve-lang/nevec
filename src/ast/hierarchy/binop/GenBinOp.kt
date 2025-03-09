package ast.hierarchy.binop

import ast.hierarchy.expr.Expr
import file.span.Loc

class GenBinOp(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) {
    fun bitwise() = BinOp.Bitwise(loc, left, operator, right)
    fun arith() = BinOp.Arith(loc, left, operator, right)
    fun comp() = BinOp.Comp(loc, left, operator, right)
}

fun BinOp.Bitwise.abstract() = GenBinOp(loc, left, operator, right)

fun BinOp.Comp.abstract() = GenBinOp(loc, left, operator, right)

fun BinOp.Arith.abstract() = GenBinOp(loc, left, operator, right)