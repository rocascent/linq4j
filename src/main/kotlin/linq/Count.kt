package linq

fun <TSource> Sequence<TSource>.longCount(): Long {
    var count = 0L

    this.forEach { _ -> count++ }

    return count
}

fun <TSource> Sequence<TSource>.longCount(predicate: (TSource) -> Boolean): Long {
    var count = 0L

    for (item in this) {
        if (predicate(item)) {
            count++
        }
    }

    return count
}