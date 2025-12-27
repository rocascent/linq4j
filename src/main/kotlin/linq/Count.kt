package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource> count(source: Enumerable<TSource>?): Int {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (source is ArrayEnumerable<TSource>) {
        return source.size
    }

    if (source is AbstractIterator<TSource>) {
        return source.itCount()
    }

    var count = 0
    source.enumerator().use {
        while (it.moveNext()) {
            count++
        }
        return count
    }
}

fun <TSource> count(source: Enumerable<TSource>?, predicate: ((TSource) -> Boolean)?): Int {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.Predicate)
    }

    var count = 0
    for (item in source) {
        if (predicate(item)) {
            count++
        }
    }
    return count
}

fun <TSource> longCount(source: Enumerable<TSource>?): Long {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    var count = 0L
    source.enumerator().use {
        while (it.moveNext()) {
            count++
        }
        return count
    }
}

fun <TSource> longCount(source: Enumerable<TSource>?, predicate: ((TSource) -> Boolean)?): Long {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.Predicate)
    }

    var count = 0L
    for (item in source) {
        if (predicate(item)) {
            count++
        }
    }
    return count
}