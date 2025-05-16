package parse.meta

import ast.info.impl.Infoful
import cli.Options
import file.span.Loc
import meta.Meta
import meta.comp.MetaComp
import meta.comp.asserts.MetaAssert
import meta.fail.MetaFail
import meta.input.Input
import meta.result.MetaResult
import meta.target.Target
import parse.ctx.ParseCtx
import parse.err.ParseErr
import parse.err.ParseResult
import parse.help.TinyParse
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
object ParseMeta : TinyParse<Pair<Infoful, Target>, Meta> {
    override fun parse(ctx: ParseCtx, data: Pair<Infoful, Target>): ParseResult<Meta> {
        val allMeta = parseAllMeta(ctx, data)
        if (allMeta.size == 1) {
            return ParseResult.Success(allMeta.first().or(Meta.empty()), ctx)
        }

        val reduced = allMeta.reduce { a, b -> a + b }

        return when (reduced) {
            is MetaResult.Success -> ParseResult.Success(reduced.meta, ctx)
            is MetaResult.Fail -> ParseResult.SemiFail(Meta.empty(), ctx)
        }
    }
    
    private fun parseAllMeta(ctx: ParseCtx, data: Pair<Infoful, Target>): List<MetaResult> {
        val (node, to) = data
        val parsed = parseMeta(ctx, to)

        if (parsed.isDummy()) {
            return listOf(parsed)
        }

        if (parsed is MetaResult.Fail) {
            ParseErr.metaFail(node.loc(), parsed.reason)?.let(ctx::showMsg)
        }

        return listOf(parsed) + parseAllMeta(ctx, data)
    }

    private fun parseMeta(ctx: ParseCtx, to: Target): MetaResult {
        return if (!ctx.check(TokKind.META_ASSERT))
            MetaFail.Dummy.wrap()
        else
            parseMetaComp(ctx, to)
    }

    private fun parseMetaComp(ctx: ParseCtx, to: Target): MetaResult {
        return when (ctx.kind()) {
            TokKind.META_ASSERT -> parseAssert(ctx, to)

            else -> throw IllegalArgumentException(
                "`parseMeta` may only be called when a `TokKind.META_ASSERT` or `TokKind.META_ANNOT`."
            )
        }
    }

    private fun parseAssert(ctx: ParseCtx, to: Target): MetaResult {
        val options = ctx.cliCtx.options
        if (!options.isEnabled(Options.META_ASSERTS)) {
            return MetaFail.NotEnabled(ctx.here()).wrap()
        }

        val from = ctx.consumeHere()
        val id = ctx.consume(TokKind.ID) ?: return parseFail(ctx)

        val input = determineInput(ctx)

        return when (id.lexeme) {
            "type" -> parseTypeAssert(ctx, from, to, input)
            else -> unknownAssert(id)
        }
    }

    private fun parseTypeAssert(ctx: ParseCtx, from: Loc, to: Target, inputGiven: PossibleInput): MetaResult {
        val (input, newCtx) = parsePossibleInput(
            ctx,
            inputGiven,
            with = ParseType::parse
        ) ?: return inputFail(ctx)

        val loc = from.tryMerge(with = newCtx.here())

        closingBracket(newCtx)
        val assert = MetaAssert.TypeAssert(input, loc)

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

    private fun determineInput(ctx: ParseCtx): PossibleInput {
        return if (ctx.match(TokKind.RBRACKET)) {
            PossibleInput.Missing
        } else {
            ctx.consume(TokKind.EQ)
            PossibleInput.Given
        }
    }

    private fun <T> parsePossibleInput(
        ctx: ParseCtx,
        input: PossibleInput,
        with: (ParseCtx, Unit) -> ParseResult<T>
    ): Pair<Input<T>, ParseCtx>? {
        return if (input.isGiven()) {
            val parsed = with(ctx.new(), Unit)
            val newCtx = parsed.newCtx()

            if (!parsed.isSuccess()) {
                closingBracket(ctx)
                return null
            }

            Input.Present<T>(parsed.success()!!) to newCtx
        } else {
            closingBracket(ctx)
            Input.Absent<T>() to ctx
        }
    }

    private fun closingBracket(ctx: ParseCtx): Tok? {
        return ctx.consume(TokKind.RBRACKET)
    }

    private fun parseFail(ctx: ParseCtx): MetaResult {
        return MetaFail.Parse(ctx.here()).wrap()
    }

    private fun inputFail(ctx: ParseCtx): MetaResult {
        return MetaFail.Input(ctx.here()).wrap()
    }
}