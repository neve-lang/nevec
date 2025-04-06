package type.pretty

import ast.pretty.Pretty
import type.Type
import util.extension.capitalize

/**
 * Pretty-printer helper for types.
 */
object PrettyType {
    fun pretty(type: Type) = when (type) {
        is Type.PoisonedType -> type.poison.toString().lowercase().capitalize()
        is Type.RefineType -> prettyRefine(type)
        is Type.RecType -> type.name()
        is Type.PrimType -> type.name()
        is Type.HintedType -> type.name()
        is Type.ChanceType -> TODO()
        is Type.FreeType -> TODO()
        is Type.GenType -> TODO()
    }

    private fun prettyRefine(type: Type.RefineType): String {
        return "${pretty(type)} where ${Pretty.visitExpr(type.refine.predicate)}"
    }
}