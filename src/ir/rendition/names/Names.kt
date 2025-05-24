package ir.rendition.names

import ir.term.TermLike
import ir.term.id.TermId

/**
 * Finds suitable unique names for IR entities, such as [Blocks][ir.structure.block.Block] and [Terms][TermLike],
 * by attaching a discriminator suffix—usually an integer—to the [desired name][TermLike.desiredName].
 *
 * Intended to work with [Rendition][ir.rendition.Rendition].
 *
 * @see ir.rendition.Rendition
 */
class Names {
    private val nextDiscriminator = mutableMapOf<String, Int>()
    private val namesById = mutableMapOf<TermId, String>()

    /**
     * Finds a suitable unique name for [desired] based on its [id].
     */
    fun findNameFor(id: Int, desired: String): String {
        return namesById[id] ?: findNewSuitableName(id, desired)
    }

    private fun findNewSuitableName(id: Int, desired: String): String {
        val discriminator = findDiscriminator(desired)
        return (desired + discriminator.toString()).also {
            namesById[id] = it
        }
    }

    private fun findDiscriminator(desired: String): Int {
        if (nextDiscriminator.containsKey(desired)) {
            return nextDiscriminator[desired]!!.also {
                update(desired, previousDiscriminator = it)
            }
        }

        return 0.also {
            update(desired)
        }
    }

    private fun update(desired: String, previousDiscriminator: Int = 0) {
        nextDiscriminator[desired] = previousDiscriminator + 1
    }
}