package ast.info

import ast.info.impl.Infoful
import file.span.Loc
import meta.Meta
import meta.asserts.MetaAssert
import type.Type

/**
 * Encapsulates both a [Loc] and a [Type] into a single data class, to reduce the number of
 * properties that AST nodes have to hold onto.
 *
 * Note that, because this data class requires a [Type], it is not used in AST nodes that don’t need
 * a type attached to them.
 *
 * @property loc The AST node’s source code location.
 * @property type The AST node’s type.
 */
data class Info(
    private val loc: Loc,
    private val type: Type,
    private val meta: Meta
) : Infoful {
    companion object {
        /**
         * Creates an [Info] based on the [Loc] given.
         *
         * @param loc The [Loc] given.
         *
         * @return an [Info] with [loc] as the [Loc] and [Type.unresolved] as [type].
         */
        fun at(loc: Loc): Info {
            return Info(loc, Type.unresolved(), Meta.empty())
        }
    }

    /**
     * Mutates the current [Info] object and adds a [MetaAssert] to its [Meta] component.
     *
     * @param assert The meta assert in question.
     */
    fun add(assert: MetaAssert<*>) {
        meta.add(assert)
    }

    override fun loc(): Loc {
        return loc
    }

    override fun type(): Type {
        return type
    }

    override fun meta(): Meta {
        return meta
    }
}