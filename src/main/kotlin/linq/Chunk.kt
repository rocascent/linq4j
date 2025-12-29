package linq

import linq.collections.enumerable.IterableEnumerable
import linq.collections.enumerable.SequenceEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import linq.exception.throwArgumentOutOfRangeException

fun <TSource> chunk(source: Enumerable<TSource>?, size: Int): Enumerable<Enumerable<TSource>> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (size < 1) {
        throwArgumentOutOfRangeException(ExceptionArgument.Size)
    }

    return SequenceEnumerable(enumerableChunkIterator(source, size))
}

private fun <TSource> enumerableChunkIterator(source: Enumerable<TSource>, size: Int): Sequence<Enumerable<TSource>> =
    sequence {
        source.enumerator().use {
            if (it.moveNext()) {
                var i: Int
                do {
                    val array = mutableListOf<TSource>()

                    array.add(it.current)
                    i = 1
                    while (i < size && it.moveNext()) {
                        array.add(it.current)
                        i++
                    }
                    yield(IterableEnumerable(array))
                } while (i >= size && it.moveNext())
            }
        }
    }