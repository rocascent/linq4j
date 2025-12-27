package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.collections.enumerable.EmptyEnumerable
import linq.collections.enumerable.SequenceEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource> skip(source: Enumerable<TSource>?, count: Int): Enumerable<TSource> {
    var count = count
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    if (count <= 0) {
        if (source is AbstractIterator<TSource>) {
            return source
        }

        count = 0
    } else if (source is AbstractIterator<TSource>) {
        return source.itSkip(count) ?: EmptyEnumerable()
    }

    return if (source is ArrayEnumerable<TSource>) {
        ArraySkipTakeIterator(source, count, Int.MAX_VALUE)
    } else {
        EnumerableSkipTakeIterator(source, count, -1)
    }
}

fun <TSource> skipWhile(source: Enumerable<TSource>?, predicate: ((TSource) -> Boolean)?): Enumerable<TSource> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }
    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.Predicate)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(skipWhileIterator(source, predicate))
}

private fun <TSource> skipWhileIterator(
    source: Enumerable<TSource>,
    predicate: (TSource) -> Boolean
): Sequence<TSource> =
    sequence {
        source.enumerator().use {
            while (it.moveNext()) {
                val element = it.current
                if (!predicate(element)) {
                    yield(element)
                    while (it.moveNext()) {
                        yield(it.current)
                    }

                    return@sequence
                }
            }
        }
    }

fun <TSource> skipWhile(source: Enumerable<TSource>?, predicate: ((TSource, Int) -> Boolean)?): Enumerable<TSource> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }
    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.Predicate)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(skipWhileIterator(source, predicate))
}

private fun <TSource> skipWhileIterator(
    source: Enumerable<TSource>,
    predicate: (TSource, Int) -> Boolean
): Sequence<TSource> =
    sequence {
        source.enumerator().use {
            var index = -1
            while (it.moveNext()) {
                index++
                val element = it.current
                if (!predicate(element, index)) {
                    yield(element)
                    while (it.moveNext()) {
                        yield(it.current)
                    }

                    return@sequence
                }
            }
        }
    }
