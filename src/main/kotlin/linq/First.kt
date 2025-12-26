package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import linq.exception.throwNoElementsException

fun <TSource> first(source: Enumerable<TSource>): TSource {
    return tryGetFirst(source) ?: throwNoElementsException()
}

fun <TSource> first(source: Enumerable<TSource>, predicate: (TSource) -> Boolean): TSource {
    return tryGetFirst(source, predicate) ?: throwNoElementsException()
}

fun <TSource> firstOrDefault(source: Enumerable<TSource>): TSource? {
    return tryGetFirst(source)
}

fun <TSource> firstOrDefault(source: Enumerable<TSource>, defaultValue: TSource): TSource {
    return tryGetFirst(source) ?: defaultValue
}

fun <TSource> firstOrDefault(
    source: Enumerable<TSource>,
    predicate: (TSource) -> Boolean
): TSource? {
    return tryGetFirst(source, predicate)
}

fun <TSource> firstOrDefault(
    source: Enumerable<TSource>,
    predicate: (TSource) -> Boolean,
    defaultValue: TSource
): TSource {
    return tryGetFirst(source, predicate) ?: defaultValue
}

private fun <TSource> tryGetFirst(source: Enumerable<TSource>?): TSource? {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }
    if (source is AbstractIterator<TSource>) {
        return source.tryGetFirst()
    }
    return tryGetFirstNonIterator(source)
}

fun <TSource> tryGetFirstNonIterator(source: Enumerable<TSource>): TSource? {
    if (source is ArrayEnumerable<TSource>) {
        if (source.size > 0) {
            return source[0]
        }
    } else {
        source.enumerator().use {
            if (it.moveNext()) {
                return it.current
            }
        }
    }
    return null
}

fun <TSource> tryGetFirst(source: Enumerable<TSource>?, predicate: ((TSource) -> Boolean)?): TSource? {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.predicate)
    }

    for (element in source) {
        if (predicate(element)) {
            return element
        }
    }

    return null
}