package type.gen

import type.Type
import type.WrappedType

data class Free(val id: Int, val level: Int) : WrappedType {
    override fun wrap() = Type.FreeType(this)
}