package parse.ctx

import ctx.Ctx
import parse.tok.Window
import type.table.TypeTable

/**
 * Tiny data class intended to be shipped around Parse helpers.
 */
data class ParseCtx(val window: Window, val typeTable: TypeTable, val ctx: Ctx)