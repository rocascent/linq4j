package linq;

import linq.collections.enumerable.ArrayEnumerable;
import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

public class Take {
    static <TSource> Enumerable<TSource> take(Enumerable<TSource> source, int count) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (count <= 0 || Enumerable.isEmptyArray(source)) {
            return Enumerable.empty();
        }

        if (source instanceof AbstractIterator<TSource> iterator) {
            var take = iterator.take(count);
            return take == null ? Enumerable.empty() : take;
        }
        if (source instanceof ArrayEnumerable<TSource> array) {
            return new SkipTake.ArraySkipTakeIterator<>(array, 0, count - 1);
        }
        return new SkipTake.EnumerableSkipTakeIterator<>(source, 0, count - 1);
    }
}
