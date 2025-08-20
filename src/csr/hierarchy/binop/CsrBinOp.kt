package csr.hierarchy.binop

import ast.hierarchy.binop.operator.ArithOperator
import ast.hierarchy.binop.operator.BitwiseOperator
import ast.hierarchy.binop.operator.CompOperator
import ast.hierarchy.binop.operator.ConcatOperator
import csr.hierarchy.expr.CsrExpr

sealed class CsrBinOp {
    data class Bitwise(val left: CsrExpr, val operator: BitwiseOperator, val right: CsrExpr) : CsrBinOp()
    data class Arith(val left: CsrExpr, val operator: ArithOperator, val right: CsrExpr) : CsrBinOp()
    data class Comp(val left: CsrExpr, val operator: CompOperator, val right: CsrExpr) : CsrBinOp()
    data class Concat(val left: CsrExpr, val operator: ConcatOperator, val right: CsrExpr) : CsrBinOp()
}