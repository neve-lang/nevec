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
}