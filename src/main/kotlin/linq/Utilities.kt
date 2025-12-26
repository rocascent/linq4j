package linq

infix fun <TSource> ((TSource) -> Boolean).and(other: (TSource) -> Boolean): (TSource) -> Boolean {
    return { this(it) && other(it) }
}

infix fun <TSource, TMiddle, TResult> ((TSource) -> TMiddle).then(other: (TMiddle) -> TResult): (TSource) -> TResult {
    return { other(this(it)) }
}