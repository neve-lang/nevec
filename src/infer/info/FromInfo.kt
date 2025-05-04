package infer.info

import ast.hierarchy.expr.Expr
import type.Type
import ast.info.Info
import infer.Infer
import infer.unify.Unify

/**
 * Tiny little helper class that abstracts away the process of inferring the new [Type] of an [Info] object.
 *
 * It is useful, because, in the Neve compiler, the process of inferring an expression’s type involves unifying the
 * inferred type with the assumed type (often [Unresolved][type.poison.Poison.Unresolved], and sometimes
 * [Hinted][type.hinted.Hinted]).
 *
 * @see Info
 * @see type.poison.Poison
 * @see type.hinted.Hinted
 */
data class FromInfo(private val info: Info) {
    /**
     * Infers the type of [from] using [with].
     *
     * @param from The expression to be inferred.
     * @param with The [Infer] object of the [SemResolver][check.sem.SemResolver].
     *
     * @return A new [Info] data class with the new type.
     */
    fun infer(from: Expr, with: Infer): Info {
        return Info(
            info.loc(),
            Unify(info.type(), with.visit(from)).infer(),
            info.meta()
        )
    }
}