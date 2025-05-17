package meta.input

/**
 * Provides a [requiresInput] method that returns whether the [meta component][meta.comp.MetaComp] requires an input
 * or not.
 *
 * In cases where input is optional for a meta component, `false` should be returned.
 */
interface ReqsInput {
    /**
     * @return Whether the implementor meta component requires an input or not.
     *
     * In cases where input is optional for a meta component, `false` should be returned.
     */
    fun requiresInput(): Boolean
}

/*
--- ...
open idea ReqsInput
  fun requires_input -> Bool
end
 */