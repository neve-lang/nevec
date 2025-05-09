package parse.type

import cli.Options
import parse.ctx.ParseCtx
import tok.TokKind
import type.Type
import type.gen.Applied
import type.gen.Free
import type.gen.arg.TypeArgs
import type.poison.Poison

/**
 * Helper class that takes care of parsing types.
 */
class ParseType(ctx: ParseCtx) {
    private val window = ctx.window
    private val typeTable = ctx.typeTable
    private val options = ctx.cliCtx.options

    fun parse(): Type? {
        return parseType()
    }

    private fun parseType() = when (window.kind()) {
        // TODO: add support for alternative table syntax: [K: V]
        TokKind.APOSTROPHE -> parseFree()
        TokKind.TILDE -> parsePoison()
        else -> parseNamed()
    }

    private fun parseFree(): Type? {
        if (!options.isEnabled(Options.COMPILER_TYPES)) {
            return null
        }

        window.advance()
        val id = window.take(TokKind.INT) ?: return null

        return Free(
            id.lexeme.toInt(),
            0
        ).covered()
    }

    private fun parsePoison(): Type? {
        if (!options.isEnabled(Options.COMPILER_TYPES)) {
            return null
        }

        window.advance()
        val id = window.take(TokKind.ID) ?: return null

        return Poison.fromName(id.lexeme)?.covered()
    }

    private fun parseNamed(): Type? {
        val name = window.take(TokKind.ID) ?: return null
        val type = typeTable.find(name.lexeme) ?: return Type.undefined(name.lexeme)

        return parseArgs(after = type)
    }

    private fun parseArgs(after: Type): Type? {
        return if (window.match(TokKind.LPAREN)) {
            parseEachArg(after).let {
                if (window.match(TokKind.RPAREN))
                    it
                else
                    null
            }
        } else {
            parseEachArg(after)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseEachArg(after: Type): Type? {
        if (!window.kind().isTypeStarter()) {
            return after
        }

        val types = mutableListOf(parseType())
        while (window.match(TokKind.COMMA)) {
            types.add(parseType())
        }

        return if (types.any { it == null })
            null
        else
            Applied(
                TypeArgs(types.toList() as List<Type>),
                after
            ).covered()
    }
}