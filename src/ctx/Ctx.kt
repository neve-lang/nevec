package ctx

import cli.Options

/**
 * The Neve compiler’s **context object**.
 *
 * Stores data regarding compiler settings and command-line arguments.
 *
 * @property options The CLI options the program was launched with.
 */
data class Ctx(val options: Options)