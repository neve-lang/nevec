package evo

import check.analys.Analys
import ast.hierarchy.expr.Expr
import type.chance.Chance
import util.extension.zip

sealed class Evo {
    data class Simple(val transform: Expr) : Evo()
    data class Combined(val first: Evo, val second: Evo) : Evo()

    fun applied(to: Chance): Chance = when (this) {
        is Simple -> Analys.visit(transform.zip(to.wrap()))
        is Combined -> first.applied(to).also { second.applied(to) }
    }

    operator fun plus(other: Evo) = Combined(this, other)

    companion object {
        fun id() = Simple(Expr.empty())
    }
}