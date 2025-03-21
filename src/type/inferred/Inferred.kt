package type.inferred

import type.Type.InferredType
import java.lang.reflect.Type

/**
 * Used to distinguish inferred types from user-hinted types during the type-checking phase.
 *
 * When an [InferredType] is confirmed to be valid, the type is extracted using [accept].
 */
data class Inferred(val type: Type) {
    fun accept() = type
}