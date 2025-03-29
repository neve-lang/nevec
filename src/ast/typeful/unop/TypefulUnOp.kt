package ast.typeful.unop

import ast.typeful.FirstType
import file.span.Loc
import type.chance.Chance

sealed class TypefulUnOp : FirstType {
    data class Not(val operand: Chance, val loc: Loc) : TypefulUnOp()
    data class Neg(val operand: Chance, val loc: Loc) : TypefulUnOp()

    override fun firstType() = when (this) {
        is Not -> operand
        is Neg -> operand
    }
}