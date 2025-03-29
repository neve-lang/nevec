package type.chance.field

import type.chance.Chances

class FieldChances(vararg fields: Pair<String, Chances>) {
    private val fields = mapOf(*fields)
}