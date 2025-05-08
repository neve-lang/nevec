package parse.meta

import file.span.Loc
import meta.Meta
import meta.comp.MetaComp
import meta.comp.asserts.MetaAssert
import meta.fail.MetaFail
import meta.result.MetaResult
import meta.target.Target
import parse.ctx.ParseCtx
import parse.type.ParseType
import tok.Tok
import tok.TokKind

/**
 * Helper parser that takes care of parsing [MetaComps][MetaComp], into [Meta] data classes.
 *
 * @return A [MetaResult].
 *
 * @see MetaComp
 * @see MetaResult
 */
class ParseMeta(private val ctx: ParseCtx) {
    private val window = ctx.window

    /**
     * Tries to parse a [meta component][MetaComp].
     *
     * @return A [MetaResult.Success] if successful, a [MetaResult.Fail] otherwise.
     */
    fun parse(to: Target): MetaResult {
        return if (!window.check(TokKind.META_ASSERT))
            Meta.empty().wrap()
        else
            parseMeta(to)
    }

    private fun parseMeta(to: Target): MetaResult {
        return when (window.kind()) {
            TokKind.META_ASSERT -> parseAssert(to)

            else -> throw IllegalArgumentException(
                "`parseMeta` may only be called when a `TokKind.META_ASSERT` or `TokKind.META_ANNOT`."
            )
        }
    }

    private fun parseAssert(to: Target): MetaResult {
        val from = window.here()
        window.advance()
        val id = window.take(TokKind.ID) ?: return parseFail()

        window.take(TokKind.EQ) ?: return parseFail()

        return when (id.lexeme) {
            "type" -> parseTypeAssert(from, to)
            else -> unknownAssert(id)
        }
    }

    private fun parseTypeAssert(from: Loc, to: Target): MetaResult {
        val type = ParseType(ctx).parse() ?: return inputFail()
        val loc = from.tryMerge(with = window.here())

        val assert = MetaAssert.TypeAssert(type, loc)
        closingBracket() ?: return parseFail()

        return applyOrFail(assert, to, loc)
    }

    private fun unknownAssert(id: Tok): MetaResult {
        return MetaFail.Name(id.loc, id.lexeme).wrap()
    }

    private fun applyOrFail(comp: MetaComp, to: Target, at: Loc): MetaResult {
        return if (comp.appliesTo(to))
            Meta.from(comp).wrap()
        else
            MetaFail.Target(at).wrap()
    }

    private fun closingBracket(): Tok? {
        return window.take(TokKind.RBRACKET)
    }

    private fun parseFail(): MetaResult {
        return MetaFail.Parse(window.here()).wrap()
    }

    private fun inputFail(): MetaResult {
        return MetaFail.Input(window.here()).wrap()
    }
}