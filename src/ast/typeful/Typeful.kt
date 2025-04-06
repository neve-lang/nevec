package ast.typeful

import type.chance.ChanceWrapper

/**
 * Simple interface for the typeful AST that returns the first type given, i.e. the patient type.
 *
 * # Example
 *
 * ```kt
 * val typeInQuestion = typefulUnOp.patientType()
 * ```
 */
interface Typeful {
    fun patientType(): ChanceWrapper
}