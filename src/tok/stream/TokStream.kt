package tok.stream

import tok.Tok

/**
 * Represents a stream of tokens.
 *
 * A [TokStream] may only be used for one individual Neve module.
 */
data class TokStream(val toks: List<Tok>)