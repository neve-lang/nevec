package ast.hierarchy.binop

import ast.hierarchy.expr.Expr
import file.span.Loc

sealed class BinOp {
    data class Bitwise(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) : Expr()
    data class Arith(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) : Expr()
    data class Comp(val loc: Loc, val left: Expr, val operator: Operator, val right: Expr) : Expr()
    data class Concat(val loc: Loc, val left: Expr, val right: Expr) : Expr()
}