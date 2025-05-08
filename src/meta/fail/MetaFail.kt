package meta.fail

import file.span.Loc
import meta.comp.MetaComp
import meta.result.MetaResult

/**
 * Represents the kinds of failures that can happen when dealing with **meta components**, like parsing errors,
 * [meta.target.Target] errors, etc.
 *
 * @see meta.target.Target
 */
sealed class MetaFail {
    /**
     * Represents a failure that occurred during parsing of a meta component, i.e. malformed syntax.
     *
     * An example of a [Parse] meta failure could be:
     *
     * ```
     * "Hello, world!" @[type] -- missing `==` and input.
     * ```
     *
     * @property loc The location where the parsing error occurred.
     */
    data class Parse(val loc: Loc) : MetaFail()

    /**
     * Represents a failure with **meta component input**—when the input given to the meta component is malformed.
     *
     * An example of an [Input] meta failure could be:
     *
     * ```
     * "Hello, world!" @[type == 1 + 2]
     * ```
     *
     * @property loc The location where parsing failed.
     */
    data class Input(val loc: Loc) : MetaFail()

    /**
     * Represents a failure when a meta component doesn’t exist for a given name.
     *
     * An example of a [Name] meta failure could be:
     *
     * ```
     * "Hello, world!" @[len == 42] -- the `len` meta assertion doesn’t exist.
     * ```
     *
     * @property loc The location where the unexpected name was found.
     * @property name The name in question.
     */
    data class Name(val loc: Loc, val name: String) : MetaFail()

    /**
     * Represents a meta failure when a meta component cannot be applied to a certain AST node or parse rule.
     *
     * @property loc The location of the [MetaComp] where the [MetaFail] occurred.
     *
     * @see meta.target.Target
     */
    data class Target(val loc: Loc) : MetaFail()

    /**
     * Represents a meta failure that occurs when `--meta-asserts` is not enabled.
     *
     * @property loc The location of the [MetaComp].
     *
     * @see cli.Options.META_ASSERTS
     */
    data class NotEnabled(val loc: Loc) : MetaFail()

    /**
     * @return A [MetaResult] wrapper around `this`.
     *
     * @see MetaResult
     */
    fun wrap(): MetaResult {
        return MetaResult.Fail(this)
    }
}