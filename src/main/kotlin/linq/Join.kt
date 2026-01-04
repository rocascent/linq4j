package linq

fun <TOuter, TInner, TKey, TResult> Sequence<TOuter>.join(
    inner: Iterable<TInner>,
    outerKeySelector: (TOuter) -> TKey,
    innerKeySelector: (TInner) -> TKey,
    resultSelector: (TOuter, TInner) -> TResult
): Sequence<TResult> = sequence {
    val iterator = this@join.iterator()

    if (iterator.hasNext()) {
        val innerLookUp = createLoopUp(inner.asSequence(), innerKeySelector);
        do {
            val item = iterator.next();
            val g = innerLookUp.getGrouping(outerKeySelector(item))
            if (g != null) {
                for (element in g) {
                    yield(resultSelector(item, element))
                }
            }
        } while (iterator.hasNext());
    }
}

fun <TOuter, TInner, TKey, TResult> Sequence<TOuter>.leftJoin(
    inner: Iterable<TInner>,
    outerKeySelector: (TOuter) -> TKey,
    innerKeySelector: (TInner) -> TKey,
    resultSelector: (TOuter, TInner?) -> TResult
): Sequence<TResult> = sequence {
    val iterator = this@leftJoin.iterator()

    if (iterator.hasNext()) {
        val innerLookUp = createLoopUp(inner.asSequence(), innerKeySelector);
        do {
            val item = iterator.next();
            val g = innerLookUp.getGrouping(outerKeySelector(item))
            if (g == null) {
                yield(resultSelector(item, null))
            } else {
                for (element in g) {
                    yield(resultSelector(item, element))
                }
            }
        } while (iterator.hasNext());
    }
}

fun <TOuter, TInner, TKey, TResult> Sequence<TOuter>.rightJoin(
    inner: Iterable<TInner>,
    outerKeySelector: (TOuter) -> TKey,
    innerKeySelector: (TInner) -> TKey,
    resultSelector: (TOuter?, TInner) -> TResult
): Sequence<TResult> = sequence {
    val iterator = inner.iterator()

    if (iterator.hasNext()) {
        val outerLookup = createLoopUp(this@rightJoin, outerKeySelector);
        do {
            val item = iterator.next();
            val g = outerLookup.getGrouping(innerKeySelector(item))
            if (g == null) {
                yield(resultSelector(null, item))
            } else {
                for (element in g) {
                    yield(resultSelector(element, item))
                }
            }
        } while (iterator.hasNext());
    }
}

fun <TOuter, TInner, TKey, TResult> Sequence<TOuter>.groupJoin(
    inner: Iterable<TInner>,
    outerKeySelector: (TOuter) -> TKey,
    innerKeySelector: (TInner) -> TKey,
    resultSelector: (TOuter, Enumerable<TInner>) -> TResult
): Sequence<TResult> = sequence {
    val iterator = this@groupJoin.iterator()
    if (iterator.hasNext()) {
        val lookUp = createLoopUp(inner.asSequence(), innerKeySelector)
        do {
            val item = iterator.next()
            yield(resultSelector(item, lookUp[outerKeySelector(item)]))
        } while (iterator.hasNext())
    }
}