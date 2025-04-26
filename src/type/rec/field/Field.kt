package type.rec.field

import type.kind.TypeKind

/**
 * Denotes a
 */
data class Field(val name: String, val type: TypeKind, val tags: List<Tag>)