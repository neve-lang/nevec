package parse.type

import cli.Options
import err.help.SimpleMsg
import parse.ctx.ParseCtx
import parse.err.ParseErr
import parse.err.ParseResult
import parse.help.TinyParse
import tok.TokKind
import type.Type
import type.gen.Applied
import type.gen.Free
import type.gen.arg.TypeArgs
import type.poison.Poison
import type.prelude.PreludeTypes
import util.extension.any

/**
 * Helper class that takes care of parsing types.
 */
object ParseType : TinyParse<Unit, Type> {
    override fun parse(ctx: ParseCtx, data: Unit): ParseResult<Type> {
        return parseType(ctx)
    }

    private fun parseType(ctx: ParseCtx) = when (ctx.kind()) {
        TokKind.APOSTROPHE -> parseFree(ctx)
        TokKind.TILDE -> parsePoison(ctx)
        TokKind.LBRACKET -> parseListOrTable(ctx)
        else -> parseNamed(ctx)
    }

    private fun parseFree(ctx: ParseCtx): ParseResult<Type> {
        val options = ctx.cliCtx.options

        if (!options.isEnabled(Options.COMPILER_TYPES)) {
            return requiresCompilerTypes(ctx)
        }

        ctx.consume()
        val id = ctx.consume(TokKind.INT) ?: return fail(ctx)

        return ParseResult.Success(
            Free(
                id.lexeme.toInt(),
                0
            ).covered(),
            ctx
        )
    }

    private fun parsePoison(ctx: ParseCtx): ParseResult<Type> {
        val options = ctx.cliCtx.options

        if (!options.isEnabled(Options.COMPILER_TYPES)) {
            return requiresCompilerTypes(ctx)
        }

        ctx.consume()
        val id = ctx.consume(TokKind.ID) ?: return fail(ctx)

        val poison = Poison.fromName(id.lexeme)
        if (poison == null) {
            // TODO: add an Example section
            ctx.showMsg(
                SimpleMsg.at(
                    id.loc,
                    msg = "no such poisoned type",
                    saying = "${id.lexeme} is not a valid poisoned type"
                )
            )

            return ParseResult.Fail(ctx)
        }

        return ParseResult.Success(poison.covered(), ctx)
    }

    private fun parseListOrTable(ctx: ParseCtx): ParseResult<Type> {
        ctx.consume()
        val type = parseType(ctx)

        if (ctx.match(TokKind.COL)) {
            return parseTable(ctx, type)
        }

        // no support for lists yet
        return fail(ctx)
    }

    private fun parseTable(ctx: ParseCtx, keyType: ParseResult<Type>): ParseResult<Type> {
        val valType = parseType(ctx)
        ctx.consume(TokKind.RBRACKET)

        return if ((keyType to valType).any { it is ParseResult.Fail })
            fail(ctx)
        else
            ParseResult.Success(
                Applied(
                    TypeArgs.from(keyType.success()!!, valType.success()!!),
                    PreludeTypes.TABLE
                ).covered(),
                ctx
            )
    }

    private fun parseNamed(ctx: ParseCtx): ParseResult<Type> {
        val name = ctx.consume(TokKind.ID) ?: return fail(ctx)
        val type = ctx.typeTable.find(name.lexeme)
            ?: return ParseResult.Success(Type.undefined(name.lexeme), ctx)

        return parseArgs(ctx, after = type)
    }

    private fun parseArgs(ctx: ParseCtx, after: Type): ParseResult<Type> {
        return if (ctx.match(TokKind.LPAREN)) {
            parseEachArg(ctx, after).let {
                ctx.consume(TokKind.RPAREN) ?: return ParseResult.Fail(ctx)
                return it
            }
        } else {
            parseEachArg(ctx, after)
        }
    }

    private fun parseEachArg(ctx: ParseCtx, after: Type): ParseResult<Type> {
        if (!ctx.kind().isTypeStarter()) {
            return ParseResult.Success(after, ctx)
        }

        val types = mutableListOf(parseType(ctx))
        while (ctx.match(TokKind.COMMA)) {
            types.add(parseType(ctx))
        }

        return if (types.any { it is ParseResult.Fail })
            ParseResult.Fail(ctx)
        else
            ParseResult.Success(
                Applied(
                    TypeArgs(types.map { it.success()!! }),
                    after
                ).covered(),
                ctx
            )
    }

    private fun requiresCompilerTypes(ctx: ParseCtx): ParseResult<Type> {
        ctx.showMsg(
            ParseErr.notEnabled(
                feature = "compiler types",
                arg = "--compiler-types",
                ctx.here()
            )
        )

        return fail(ctx)
    }

    private fun fail(ctx: ParseCtx): ParseResult.Fail<Type> {
        return ParseResult.Fail(ctx)
    }
}