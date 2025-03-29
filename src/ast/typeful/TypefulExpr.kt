package ast.typeful

import ast.typeful.binop.TypefulBinOp
import ast.typeful.unop.TypefulUnOp
import file.span.Loc
import type.chance.Chance

/**
 * AST Expression nodes that store operands as [firstType.chance.Chance] types, effectively making every node flat.
 *
 * Intended to work with the [check.analys.Analys] module.
 */
sealed class TypefulExpr : FirstType {
    data class Show(val operand: Chance, val loc: Loc) : TypefulExpr()
    data class UnOpExpr(val unOp: TypefulUnOp, val loc: Loc) : TypefulExpr()
    data class BinOpExpr(val binOp: TypefulBinOp, val loc: Loc) : TypefulExpr()

    override fun firstType(): Chance = when (this) {
        is Show -> operand
        is UnOpExpr -> unOp.firstType()
        is BinOpExpr -> binOp.firstType()
    }
}