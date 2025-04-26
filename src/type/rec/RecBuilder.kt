package type.rec

import file.module.Module
import type.gen.param.TypeParams
import type.rec.field.Field
import type.rec.field.Fields

/**
 * Simplifies the process of building a [Rec] data class.
 *
 * @see Rec
 */
class RecBuilder {
    private var module: Module? = null
    private var name: String? = null
    private var fields: Fields? = null
    private var params: TypeParams? = null

    /**
     * @return A [RecBuilder] from [Module.PRELUDE].
     */
    fun prelude(name: String): RecBuilder {
        return from(Module.PRELUDE).named(name)
    }

    /**
     * @return A [RecBuilder] with [module].
     */
    fun from(module: Module): RecBuilder {
        return apply { this.module = module }
    }

    /**
     * @return A [RecBuilder] with [name].
     */
    fun named(name: String): RecBuilder {
        return apply { this.name = name }
    }

    /**
     * @return A [RecBuilder] with [fields].
     */
    fun fields(vararg fields: Field): RecBuilder {
        return apply { this.fields = Fields(*fields) }
    }

    /**
     * @return A [RecBuilder] with [params] as type parameters.
     */
    fun params(params: TypeParams): RecBuilder {
        return apply { this.params = params }
    }

    /**
     * Builds the [RecBuilder] into a [Rec].
     *
     * Building will be successful **if and only if**:
     *
     * - [module] is set (using [from])
     * - [name] is set (using [name])
     *
     * [fields] will be set to an empty [Field] class if none are given.
     * [params] will be set to [TypeParams.none] if none are given.
     *
     * @return A built [Rec].
     *
     * @see Field
     * @see TypeParams
     */
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