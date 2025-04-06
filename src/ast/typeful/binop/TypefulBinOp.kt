package ast.typeful.binop

import ast.hierarchy.binop.Operator
import ast.typeful.Typeful
import file.span.Loc
import type.chance.ChanceWrapper

sealed class TypefulBinOp : Typeful {
    data class Bitwise(val left: ChanceWrapper, val operator: Operator, val right: ChanceWrapper, val loc: Loc) : TypefulBinOp()
    data class Arith(val left: ChanceWrapper, val operator: Operator, val right: ChanceWrapper, val loc: Loc) : TypefulBinOp()
    data class Comp(val left: ChanceWrapper, val operator: Operator, val right: ChanceWrapper, val loc: Loc) : TypefulBinOp()
    data class Concat(val left: ChanceWrapper, val right: ChanceWrapper, val loc: Loc) : TypefulBinOp()

    override fun patientType() = when (this) {
        is Bitwise -> left
        is Arith -> left
        is Comp -> left
        is Concat -> left
    }

    fun operands() = when (this) {
        is Bitwise -> Pair(left, right)
        is Arith -> Pair(left, right)
        is Comp -> Pair(left, right)
        is Concat -> Pair(left, right)
    }
}