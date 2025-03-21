package parse.binop

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.Operator
import ast.hierarchy.expr.Expr
import file.span.Loc

/**
 * Intermediate helper class that simplifies the process of building a [BinOp] node.
 */
class AnyBinOp(val loc: Loc, private val left: Expr, private val operator: Operator, private val right: Expr) {
    fun bitwise() = BinOp.Bitwise(left, operator, right, loc)
    fun arith() = BinOp.Arith(left, operator, right, loc)
    fun comp() = BinOp.Comp(left, operator, right, loc)
}