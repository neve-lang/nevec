package type.rec

import file.module.Module
import type.gen.param.TypeParams
import type.rec.field.Field
import type.rec.field.Fields

class RecBuilder {
    private var module: Module? = null
    private var name: String? = null
    private var fields: Fields? = null
    private var params: TypeParams? = null

    fun from(module: Module) = apply { this.module = module }

    fun named(name: String) = apply { this.name = name }

    fun fields(vararg fields: Field) = apply { this.fields = Fields(*fields) }

    fun params(params: TypeParams) = apply { this.params = params }

    fun build(): Rec {
        require(module != null) { "A module must be provided" }
        require(name != null) { "A name must be provided" }

        return Rec(module!!, name!!, fields ?: Fields(), params ?: TypeParams.none())
    }
}