package ast.hierarchy

import type.Type

interface Typed {
    fun type(): Type
}