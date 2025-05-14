package ast.info

import ast.info.impl.Infoful
import file.span.Loc
import meta.Meta
import meta.comp.MetaComp
import meta.target.Target
import type.Type

/**
 * Encapsulates both a [Loc] and a [Type] along with other components into a single data class, to reduce the number of
 * properties that AST nodes have to hold onto.
 *
 * Note that, because this data class requires a [Type], it is not used in AST nodes that don’t need
 * a type attached to them.
 *
 * @property loc The AST node’s source code location.
 * @property type The AST node’s type.
 * @property meta The AST node’s meta components.
 *
 * @see Type
 * @see Loc
 * @see Meta
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
     * @return an identical [Info] data class, with its [type] property replaced with [of].
     *
     * @param of The [Type] in question.
     */
    fun withType(of: Type): Info {
        return Info(loc, of, meta)
    }

    /**
     * @return an identical [Info] data class with `this` [Meta] added to [with].
     */
    operator fun plus(new: Meta): Info {
        return Info(loc, type, meta + new)
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

    override fun info(): Info {
        return this
    }

    override fun update(new: Info): Infoful {
        return new
    }
}