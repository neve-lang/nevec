package type.rec.field

import type.Type

/**
 * Denotes a field in a [Rec][type.rec.Rec].
 *
 * @property name The field’s name.
 * @property type The field’s type.
 * @property tags The field’s tags.
 *
 * @see Tag
 * @see type.rec.Rec
 */
data class Field(val name: String, val type: Type, val tags: List<Tag>)