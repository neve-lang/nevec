package type.gen

import type.impl.Compare
import type.impl.NamedType
import type.impl.Wrappable
import type.kind.TypeKind
import util.extension.prefixWith

/**
 * Represents a **quantified** or **generalized type**, i.e. a type bound by a `forall` quantifier in Hindley-Milner
 * parlance.
 *
 * In the pretty printer, those types inherit the name the [Forall][type.gen.forall.Forall] quantifier gives them.
 *
 * An example of a quantified type argument may be in the following expression:
 *
 * ```
 * let table = [:]
 * ```
 *
 * In this example, `table` has the type `∀'0. ∀'1. ['0: '1]`, where `'0` and `'1` are quantified by the
 * [Forall][type.gen.forall.Forall] type node.
 *
 * @property id The quantified type’s ID in respect to its [Forall][type.gen.forall.Forall] quantifier node.
 *
 * @see type.gen.forall.Forall
 */
data class Quant(val id: Int) : Wrappable, NamedType, Compare<Quant> {
    override fun wrap(): TypeKind {
        return TypeKind.OfQuant(this)
    }

    /**
     * Please note that the name returned only reflects the quantified type’s ID.  The
     * [pretty printer][pretty.PrettyType] may give it a different name.
     */
    override fun named(): String {
        return id.toString().prefixWith("'")
    }

    override fun isSame(other: Quant): Boolean {
        return id == other.id
    }

    override fun toString(): String {
        return named()
    }
}