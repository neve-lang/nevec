package ast.hierarchy.binop

import ast.hierarchy.expr.Expr
import file.span.Loc

class GenBinOp(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) {
    fun bitwise() = BinOp.Bitwise(loc, left, operator, right)
    fun arith() = BinOp.Arith(loc, left, operator, right)
    fun comp() = BinOp.Comp(loc, left, operator, right)
}

fun BinOp.abstract() = when (this) {
    is BinOp.Bitwise -> GenBinOp(loc, left, operator, right)
    is BinOp.Comp -> GenBinOp(loc, left, operator, right)
    is BinOp.Arith -> GenBinOp(loc, left, operator, right)
    is BinOp.Concat -> GenBinOp(loc, left, Operator.PLUS, right)
}