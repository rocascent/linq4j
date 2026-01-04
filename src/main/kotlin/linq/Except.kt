package linq

fun <TSource> Sequence<TSource>.except(other: Iterable<TSource>): Sequence<TSource> = sequence {
    val set = other.toMutableSet()

    for (element in this@except) {
        if (set.add(element)) {
            yield(element)
        }
    }
}

fun <TSource, TKey> Sequence<TSource>.exceptBy(
    other: Iterable<TKey>,
    keySelector: (TSource) -> TKey
): Sequence<TSource> = sequence {
    val set = other.toMutableSet()

    for (element in this@exceptBy) {
        if (set.add(keySelector(element))) {
            yield(element)
        }
    }
}