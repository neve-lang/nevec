package type.pretty

import ast.pretty.Pretty
import type.Type
import type.name
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
    }

    private fun prettyRefine(type: Type.RefineType): String {
        return "${pretty(type)} where ${Pretty.visitExpr(type.refine.predicate)}"
    }
}