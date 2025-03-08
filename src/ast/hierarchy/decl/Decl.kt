package ast.hierarchy.decl

import ast.hierarchy.stmt.Stmt
import file.span.Loc
import type.Type

sealed class Decl {
    data class Const(val loc: Loc, val type: Type) : Decl()
    data class Consts(val consts: List<Const>) : Decl()
    data class StmtDecl(val stmt: Stmt) : Decl()
}