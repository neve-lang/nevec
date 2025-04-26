package parse.binop

import ast.hierarchy.binop.BinOp
import ast.hierarchy.binop.operator.Operator
import ast.hierarchy.expr.Expr
import ast.info.Info

/**
 * Intermediate helper class that simplifies the process of building a [BinOp] node.
 */
class AnyBinOp(private val left: Expr, private val operator: Operator, private val right: Expr, private val info: Info) {
    /**
     * @return a [BinOp.Bitwise] node if the operator is [OfBitwise][Operator.OfBitwise].
     *
     * @throws IllegalArgumentException otherwise.
     */
    fun bitwise() = when (operator) {
        // in Neve, we’d be able to have an `assuming` claude:
        //
        // assuming self.operator is BitwiseOperator
        // fun bitwise
        //   -- ...
        // end
        //
        // hehe, i really, really can’t wait for this project ot be complete.
        is Operator.OfBitwise -> BinOp.Bitwise(left, operator.itself(), right, info)
        else -> throw IllegalArgumentException()
    }

    /**
     * @return a [BinOp.Arith] node if the operator is [OfArith][Operator.OfArith].
     *
     * @throws IllegalArgumentException otherwise.
     */
    fun arith() = when (operator) {
        is Operator.OfArith -> BinOp.Arith(left, operator.itself(), right, info)
        else -> throw IllegalArgumentException()
    }

    /**
     * @return a [BinOp.Comp] node if the operator is [OfComp][Operator.OfComp].
     *
     * @throws IllegalArgumentException otherwise.
     */
    fun comp() = when (operator) {
        is Operator.OfComp -> BinOp.Comp(left, operator.itself(), right, info)
        else -> throw IllegalArgumentException()
    }
}