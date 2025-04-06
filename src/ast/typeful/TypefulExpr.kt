package ast.typeful

import ast.typeful.binop.TypefulBinOp
import ast.typeful.unop.TypefulUnOp
import file.span.Loc
import type.chance.ChanceWrapper

/**
 * AST Expression nodes that store operands as [type.chance.ChanceWrapper] types, effectively making every node flat.  They
 * are used as [evo.Evo]s for
 *
 * Intended to work with the [check.analys.Analys] module.
 */
sealed class TypefulExpr : Typeful {
    data class Show(val operand: ChanceWrapper, val loc: Loc) : TypefulExpr()
    data class UnOpExpr(val unOp: TypefulUnOp, val loc: Loc) : TypefulExpr()
    data class BinOpExpr(val binOp: TypefulBinOp, val loc: Loc) : TypefulExpr()

    override fun patientType(): ChanceWrapper = when (this) {
        is Show -> operand
        is UnOpExpr -> unOp.patientType()
        is BinOpExpr -> binOp.patientType()
    }
}