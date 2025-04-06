/*
package evo

import check.analys.Analys
import ast.typeful.TypefulExpr
import type.chance.ChanceWrapper

sealed class Evo {
    data object Id : Evo()
    data class Transform(val by: TypefulExpr) : Evo()
    data class Combined(val first: Evo, val second: Evo) : Evo()

    fun applied(to: ChanceWrapper): ChanceWrapper = when (this) {
        is Id -> to
        is Transform -> Analys.visit(Pair(transform, to.wrap()))
        is Combined -> first.applied(to).also { second.applied(to) }
    }

    operator fun plus(other: Evo) = Combined(this, other)
}
 */