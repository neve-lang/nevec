package type.gen.param

import type.NamedType

/**
 * Represents a single generic type parameter.
 *
 * @property named The name of the type parameter.
 */
data class TypeParam(val name: String) : NamedType {
    override fun named(): String {
        return name
    }
}