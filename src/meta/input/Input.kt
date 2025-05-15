package meta.input

/**
 * Represents an optional input for [meta components][meta.comp.MetaComp], as certain meta components simply do not
 * take input (such as the [culprit meta assert][meta.comp.asserts.MetaAssert.Culprit]), whereas others make it
 * optional.
 *
 * It includes two kinds of inputs:
 *
 * - [Present]—this means that input was provided by the user.
 * - [Absent]—this means that input was *not* provided by the user.
 *
 * The way each kind of input is handled depends on the meta component itself.
 *
 * @param T The type of input to be expected by the meta component.
 */
sealed class Input<T> {
    /**
     * A present input—input explicitly given by the user—for a meta component.
     *
     * @property itself The input value itself.
     */
    data class Present<T>(val itself: T) : Input<T>()

    /**
     * An absent input—input that was *not* given by the user—for a meta component.
     */
    class Absent<T> : Input<T>()
}