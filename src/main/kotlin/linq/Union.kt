package linq

fun <TSource> Sequence<TSource>.union(other: Iterable<TSource>): Sequence<TSource> = sequence {
    val set = mutableSetOf<TSource>()

    for (element in this@union) {
        if (set.add(element)) {
            yield(element)
        }
    }

    for (element in other) {
        if (set.add(element)) {
            yield(element)
        }
    }
}

fun <TSource, TKey> Sequence<TSource>.unionBy(
    other: Iterable<TSource>,
    keySelector: (TSource) -> TKey
): Sequence<TSource> = sequence {
    val set = mutableSetOf<TKey>()

    for (element in this@unionBy) {
        if (set.add(keySelector(element))) {
            yield(element)
        }
    }

    for (element in other) {
        if (set.add(keySelector(element))) {
            yield(element)
        }
    }
}