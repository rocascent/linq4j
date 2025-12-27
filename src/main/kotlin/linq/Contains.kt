package linq

fun <TSource> containsIterate(
    source: Enumerable<TSource>,
    value: TSource,
    comparer: EqualityComparer<TSource>?
): Boolean {
    var comparer = comparer

    if (comparer == null) {
        comparer = { x, y -> x == y }
    }
    for (element in source) {
        if (comparer(element, value)) {
            return true
        }
    }

    return false
}