package linq

fun <TSource> Sequence<TSource>.intersect(other: Iterable<TSource>): Sequence<TSource> = sequence {
    val set = other.toMutableSet()

    for (element in this@intersect) {
        if (set.remove(element)) {
            yield(element)
        }
    }
}

fun <TSource, TKey> Sequence<TSource>.intersectBy(
    other: Iterable<TKey>,
    keySelector: (TSource) -> TKey
): Sequence<TSource> = sequence {
    val set = other.toMutableSet()

    for (element in this@intersectBy) {
        if (set.remove(keySelector(element))) {
            yield(element)
        }
    }
}