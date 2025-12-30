package linq

fun <TOuter, TInner, TKey, TResult> Sequence<TOuter>.groupJoin(
    inner: Iterable<TInner>,
    outerKeySelector: (TOuter) -> TKey,
    innerKeySelector: (TInner) -> TKey,
    resultSelector: (TOuter, Enumerable<TInner>) -> TResult
): Sequence<TResult> = sequence {
    val iterator = this@groupJoin.iterator()
    if (iterator.hasNext()) {
        val lookUp = Enumerable(inner.asSequence()).toLookUp(innerKeySelector)
        do {
            val item = iterator.next()
            yield(resultSelector(item, lookUp[outerKeySelector(item)]))
        } while (iterator.hasNext())
    }
}