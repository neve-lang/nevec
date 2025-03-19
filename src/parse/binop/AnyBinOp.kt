package parse.binop

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.Operator
import ast.hierarchy.expr.Expr
import file.span.Loc

class AnyBinOp(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) {
    fun bitwise() = BinOp.Bitwise(left, operator, right, loc)
    fun arith() = BinOp.Arith(left, operator, right, loc)
    fun comp() = BinOp.Comp(left, operator, right, loc)
}

fun BinOp.abstract() = when (this) {
    is BinOp.Bitwise -> AnyBinOp(loc, left, operator, right)
    is BinOp.Comp -> AnyBinOp(loc, left, operator, right)
    is BinOp.Arith -> AnyBinOp(loc, left, operator, right)
    is BinOp.Concat -> AnyBinOp(loc, left, Operator.PLUS, right)
}