package pretty

import ast.hierarchy.binop.operator.*

/**
 * Helper object for [Pretty] for [ast.hierarchy.binop.BinOp]'s [Operator]s.
 */
object PrettyOperator {
    /**
     * @return a string representing the given bitwise [operator]'s lexeme *with spaces surrounding the operator*.
     */
    fun pretty(operator: BitwiseOperator) = when (operator) {
        BitwiseOperator.SHL -> " << "
        BitwiseOperator.SHR -> " >> "
        BitwiseOperator.BIT_OR -> " bitor "
        BitwiseOperator.BIT_AND -> " bitand "
        BitwiseOperator.BIT_XOR -> " xor "
    }

    /**
     * @return a string representing the given arithmetic [operator]'s lexeme *with spaces surrounding the operator*.
     */
    fun pretty(operator: ArithOperator) = when (operator) {
        ArithOperator.ADD -> " + "
        ArithOperator.SUB -> " - "
        ArithOperator.MUL -> " * "
        ArithOperator.DIV -> " / "
    }

    /**
     * @return a string representing the given comparison [operator]'s lexeme *with spaces surrounding the operator*.
     */
    fun pretty(operator: CompOperator) = when (operator) {
        CompOperator.GTE -> " >= "
        CompOperator.LTE -> " <= "
        CompOperator.NEQ -> " != "
        CompOperator.EQ -> " == "
        CompOperator.LT -> " < "
        CompOperator.GT -> " > "
    }

    /**
     * @return a string representing the given concatenation [operator]'s lexeme *with spaces surrounding the operator*.
     */
    fun pretty(operator: ConcatOperator): String {
        return " + "
    }
}