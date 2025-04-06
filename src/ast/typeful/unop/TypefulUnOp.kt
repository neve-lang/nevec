package ast.typeful.unop

import ast.typeful.Typeful
import file.span.Loc
import type.chance.ChanceWrapper

sealed class TypefulUnOp : Typeful {
    data class Not(val operand: ChanceWrapper, val loc: Loc) : TypefulUnOp()
    data class Neg(val operand: ChanceWrapper, val loc: Loc) : TypefulUnOp()

    override fun patientType() = when (this) {
        is Not -> operand
        is Neg -> operand
    }
}