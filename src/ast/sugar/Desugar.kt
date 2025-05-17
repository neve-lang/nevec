package ast.sugar

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.operator.ArithOperator
import ast.hierarchy.binop.operator.ConcatOperator
import ast.hierarchy.expr.Expr
import type.Type
import type.comp.BothTypes
import type.prelude.PreludeTypes
import util.extension.map

/**
 * Simple helper object that takes care of desugaring **specific** AST nodes.
 */
object Desugar {
    /**
     * Desugars an [Arith][ast.hierarchy.binop.BinOp.Arith] node.
     *
     * @return A [BinOp.Concat] if successful, `null` otherwise.
     */
    fun arith(arith: BinOp.Arith): BinOp.Concat? {
        val operandTypes = arith.operands().map(Expr::type)

        if (!BothTypes.from(operandTypes).areSame()) {
            return null
        }

        if (arith.operator != ArithOperator.ADD) {
            return null
        }

        val a = operandTypes.first
        return when {
            a.isSame(PreludeTypes.STR) -> concat(of = ConcatOperator.STR, from = arith)
            else -> return null
        }
    }

    private fun concat(of: ConcatOperator, from: BinOp.Arith): BinOp.Concat {
        return BinOp.Concat(
            from.left,
            of,
            from.right,
            from.info.withType(of = Type.unresolved()),
            from.op
        )
    }
}