package util.extension

fun <A, B> Pair<A, A>.map(to: ((A) -> B)) = Pair(to(this.first), to(this.second))
