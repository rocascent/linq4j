package linq

fun <TSource, TKey> Sequence<TSource>.maxBy(selector: (TSource) -> TKey, comparer: Comparator<TKey>): TSource {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var maxElem = iterator.next()
    if (!iterator.hasNext()) return maxElem
    var maxValue = selector(maxElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (comparer.compare(maxValue, v) < 0) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return maxElem
}

fun <TSource, TKey> Sequence<TSource>.minBy(selector: (TSource) -> TKey, comparer: Comparator<TKey>): TSource {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (comparer.compare(minValue, v) > 0) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}