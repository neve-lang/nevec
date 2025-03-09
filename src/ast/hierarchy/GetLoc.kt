package ast.hierarchy

import file.span.Loc

interface GetLoc {
    fun loc(): Loc
}