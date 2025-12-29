package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import linq.exception.throwNoElementsException

fun <TSource> first(source: Enumerable<TSource>): TSource {
    return source.tryGetFirst() ?: throwNoElementsException()
}

fun <TSource> first(source: Enumerable<TSource>, predicate: (TSource) -> Boolean): TSource {
    return source.tryGetFirst(predicate) ?: throwNoElementsException()
}

fun <TSource> firstOrDefault(source: Enumerable<TSource>): TSource? {
    return source.tryGetFirst()
}

fun <TSource> firstOrDefault(source: Enumerable<TSource>, defaultValue: TSource): TSource {
    return source.tryGetFirst() ?: defaultValue
}

fun <TSource> firstOrDefault(
    source: Enumerable<TSource>,
    predicate: (TSource) -> Boolean
): TSource? {
    return source.tryGetFirst(predicate)
}

fun <TSource> firstOrDefault(
    source: Enumerable<TSource>,
    predicate: (TSource) -> Boolean,
    defaultValue: TSource
): TSource {
    return source.tryGetFirst(predicate) ?: defaultValue
}

internal fun <TSource> Enumerable<TSource>?.tryGetFirst(): TSource? {
    if (this == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }
    if (this is AbstractIterator<TSource>) {
        return this.tryGetFirst()
    }
    return tryGetFirstNonIterator(this)
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

internal fun <TSource> Enumerable<TSource>?.tryGetFirst(predicate: ((TSource) -> Boolean)?): TSource? {
    if (this == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.Predicate)
    }

    for (element in this) {
        if (predicate(element)) {
            return element
        }
    }

    return null
}