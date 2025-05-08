package type.table

import type.Type
import type.prelude.PreludeTypes

/**
 * Registers named types and allows lookup.
 *
 * Types are stored by their full-qualified types in [table].  Module and type names are simply
 * separated by dots (“.”).
 *
 * @property table The table storing the types mapped by their full-qualified names.
 */
data class TypeTable(val table: HashMap<String, Type> = hashMapOf()) {
    init {
        registerPrelude()
    }

    /**
     * Registers [type] into the type table.
     */
    fun register(type: Type) {
        table[type.named()] = type
    }

    /**
     * @return the [Type] named [name] if such a type exists in the type table, `null` otherwise.
     */
    fun find(name: String): Type? {
        return table[name]
    }

    private fun registerPrelude() {
        PreludeTypes.ALL.forEach(::register)
    }
}