package ast.hierarchy

import file.span.Loc

interface Spanned {
    fun loc(): Loc
}