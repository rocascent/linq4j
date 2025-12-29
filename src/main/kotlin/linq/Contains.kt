package linq

import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource> contains(source: Enumerable<TSource>?, value: TSource): Boolean {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (source is AbstractIterator<TSource>) {
        return source.itContains(value)
    }

    return containsIterate(source, value, null)
}

fun <TSource> contains(source: Enumerable<TSource>?, value: TSource, comparer: EqualityComparer<TSource>?): Boolean {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    return containsIterate(source, value, comparer)
}

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