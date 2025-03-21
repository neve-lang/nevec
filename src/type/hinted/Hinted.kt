package type.hinted

import type.Type

/**
 * Used to distinguish user-hinted types from inferred types during the type-checking phase.
 *
 * When an [Type.HintedType] is confirmed to be valid, the type is extracted using [accept].
 */
data class Hinted(val type: Type) {
    fun accept() = type
}