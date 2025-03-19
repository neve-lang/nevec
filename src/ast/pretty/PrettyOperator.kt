package ast.pretty

import ast.hierarchy.binop.Operator

/**
 * Helper object for [Pretty] for [ast.hierarchy.binop.BinOp]'s [Operator]s.
 */
object PrettyOperator {
    /**
     * Returns a string representing the given [operator]'s lexeme *with spaces surrounding the operator*.
     */
    fun pretty(operator: Operator) = when (operator) {
        Operator.MINUS -> " - "
        Operator.PLUS -> " + "
        Operator.MUL -> " * "
        Operator.DIV -> " / "
        Operator.GTE -> " >= "
        Operator.LTE -> " <= "
        Operator.NEQ -> " != "
        Operator.EQ -> " == "
        Operator.LT -> " < "
        Operator.GT -> " > "
        Operator.SHL -> " << "
        Operator.SHR -> " >> "
        Operator.BIT_AND -> " band "
        Operator.BIT_OR -> " bor "
        Operator.BIT_XOR -> " xor "
    }
}