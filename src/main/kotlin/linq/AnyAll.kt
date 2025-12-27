package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource> any(source: Enumerable<TSource>?): Boolean {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (source is ArrayEnumerable<TSource>) {
        return source.size != 0
    }

    if (source is AbstractIterator<TSource>) {

        return source.tryGetFirst() != null
    }

    source.enumerator().use { return it.moveNext() }
}

fun <TSource> any(source: Enumerable<TSource>?, predicate: ((TSource) -> Boolean)?): Boolean {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.Predicate)
    }

    for (element in source) {
        if (predicate(element)) {
            return true
        }
    }

    return false
}

fun <TSource> all(source: Enumerable<TSource>?, predicate: ((TSource) -> Boolean)?): Boolean {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.Predicate)
    }

    for (element in source) {
        if (!predicate(element)) {
            return false
        }
    }

    return true
}