package ast.typeful.binop

import ast.hierarchy.binop.Operator
import ast.typeful.FirstType
import file.span.Loc
import type.chance.Chance

sealed class TypefulBinOp : FirstType {
    data class Bitwise(val left: Chance, val operator: Operator, val right: Chance, val loc: Loc) : TypefulBinOp()
    data class Arith(val left: Chance, val operator: Operator, val right: Chance, val loc: Loc) : TypefulBinOp()
    data class Comp(val left: Chance, val operator: Operator, val right: Chance, val loc: Loc) : TypefulBinOp()
    data class Concat(val left: Chance, val right: Chance, val loc: Loc) : TypefulBinOp()

    override fun firstType() = when (this) {
        is Bitwise -> left
        is Arith -> left
        is Comp -> left
        is Concat -> left
    }
}