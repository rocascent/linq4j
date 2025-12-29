package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import linq.exception.throwArgumentOutOfRangeException

fun <TSource> elementAt(source: Enumerable<TSource>?, index: Int): TSource {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (source is ArrayEnumerable<TSource>) {
        return source[index]
    }

    val element = if (source is AbstractIterator<TSource>) {
        source.tryGetElementAt(index)
    } else {
        tryGetElementAtNonIterator(source, index)
    }

    if (element == null) {
        throwArgumentOutOfRangeException(ExceptionArgument.Index)
    }

    return element
}

fun <TSource> elementAtOrDefault(source: Enumerable<TSource>?, index: Int): TSource? {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    return tryGetElementAt(source, index)
}

internal fun <TSource> tryGetElementAt(source: Enumerable<TSource>, index: Int): TSource? {
    if (source is ArrayEnumerable<TSource>) {
        return if (index.toUInt() < source.size.toUInt()) {
            source[index]
        } else {
            null
        }
    }

    return if (source is AbstractIterator<TSource>) {
        source.tryGetElementAt(index)
    } else {
        tryGetElementAtNonIterator(source, index)
    }
}

internal fun <TSource> tryGetElementAtNonIterator(source: Enumerable<TSource>, index: Int): TSource? {
    var index = index
    if (index >= 0) {
        source.enumerator().use {
            while (it.moveNext()) {
                if (index == 0) {
                    return it.current
                }
                index--
            }
        }
    }

    return null
}