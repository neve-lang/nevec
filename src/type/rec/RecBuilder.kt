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

    fun prelude(name: String): RecBuilder {
        return from(Module.PRELUDE).named(name)
    }

    fun from(module: Module): RecBuilder {
        return apply { this.module = module }
    }

    fun named(name: String): RecBuilder {
        return apply { this.name = name }
    }

    fun fields(vararg fields: Field): RecBuilder {
        return apply { this.fields = Fields(*fields) }
    }

    fun params(params: TypeParams): RecBuilder {
        return apply { this.params = params }
    }

    fun build(): Rec {
        require(module != null) { "A module must be provided" }
        require(name != null) { "A name must be provided" }

        return Rec(
            module!!,
            name!!,
            fields ?: Fields(),
            params ?: TypeParams.none()
        )
    }
}