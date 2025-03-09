package lex.interpol

/**
 * Thrown when the interpolDepth of [InterpolState] exceeds [InterpolState.MAX_INTERPOL_DEPTH]; that is, 255.
 */
class InterpolTooDeepException : Exception()