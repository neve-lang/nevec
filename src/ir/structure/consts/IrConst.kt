package ir.structure.consts

/**
 * Represents the kinds of constants in the Neve IR.
 */
sealed class IrConst {
    /**
     * An integer constant.
     */
    data class OfInt(val value: Int) : IrConst()

    /**
     * A float constant.
     */
    data class OfFloat(val value: Float) : IrConst()

    /**
     * A boolean constant.
     */
    data class OfBool(val value: Boolean) : IrConst()

    /**
     * A string constant.
     */
    data class OfStr(val value: String) : IrConst()

    /**
     * A table constant.
     */
    data class OfTable(val keys: List<IrConst>, val vals: List<IrConst>) : IrConst()

    /**
     * An *empty* table constant.
     */
    data object OfEmptyTable : IrConst()

    /**
     * A nil constant.
     */
    data object OfNil : IrConst()

    /**
     * @return Whether both [IrConst] operands have the same variant, and the same value.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is IrConst) {
            return false
        }

        if (this::class != other::class) {
            return false
        }

        return when (this) {
            is OfBool -> value == (other as OfBool).value
            is OfInt -> value == (other as OfInt).value
            is OfFloat -> value == (other as OfFloat).value
            is OfStr -> value == (other as OfStr).value

            is OfNil -> other is OfNil
            is OfEmptyTable -> other is OfEmptyTable

            is OfTable -> tableEquals(this, other as OfTable)
        }
    }

    private fun tableEquals(left: OfTable, right: OfTable): Boolean {
        return (left.keys zip right.keys).all { (a, b) -> a == b } &&
                (left.vals zip right.vals).all { (a, b) -> a == b }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}