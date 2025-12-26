package linq;

import linq.collections.enumerable.ArrayEnumerable;
import linq.collections.enumerable.EmptyEnumerable
import linq.exception.ExceptionArgument;
import linq.exception.throwArgumentNullException

fun <TSource> take(source: Enumerable<TSource>?, count: Int): Enumerable<TSource> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source);
    }

    if (count <= 0 || isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    if (source is AbstractIterator<TSource>) {
        return source.itTake(count) ?: EmptyEnumerable();
    }
    if (source is ArrayEnumerable<TSource>) {
        return ArraySkipTakeIterator(source, 0, count - 1);
    }
    return EnumerableSkipTakeIterator(source, 0, count - 1);
}