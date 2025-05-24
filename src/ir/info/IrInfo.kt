package ir.info

import ast.info.impl.Infoful
import file.span.Loc

/**
 * Represents IR information.
 */
data class IrInfo(val line: Line) {
    companion object {
        /**
         * @return A new [IrInfo] based on an [Infoful].
         */
        fun from(infoful: Infoful): IrInfo {
            return from(infoful.loc())
        }

        /**
         * @return A new [IrInfo] based on a [Loc].
         */
        fun from(loc: Loc): IrInfo {
            return IrInfo(loc.line.toInt())
        }
    }
}