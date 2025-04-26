package type.table

import type.Type

/**
 * Registers named types and allows lookup.
 *
 * Types are stored by their full-qualified types in [table].  Module and type names are simply
 * separated by dots (“.”).
 *
 * @property table The table storing the types mapped by their full-qualified names.
 */
data class TypeTable(val table: HashMap<String, Type> = hashMapOf()) {
    /**
     * Registers [type] into the type table.
     */
    fun register(type: Type) {
        table[type.named()] = type
    }
}
