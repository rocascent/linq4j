package linq;

import linq.collections.enumerable.ArrayEnumerable;
import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

public class Skip {
    static <TSource> Enumerable<TSource> skip(Enumerable<TSource> source, int count) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (Enumerable.isEmptyArray(source)) {
            return Enumerable.empty();
        }

        if (count <= 0) {
            // Return source if not actually skipping, but only if it's a type from here, to avoid
            // issues if collections are used as keys or otherwise must not be aliased.
            if (source instanceof AbstractIterator<TSource>) {
                return source;
            }

            count = 0;
        } else if (source instanceof AbstractIterator<TSource> iterator) {
            var skip = iterator.skip(count);
            return skip == null ? Enumerable.empty() : skip;
        }

        if (source instanceof ArrayEnumerable<TSource> array) {
            return new SkipTake.ArraySkipTakeIterator<>(array, count, Integer.MAX_VALUE);
        } else {
            return new SkipTake.EnumerableSkipTakeIterator<>(source, count, -1);
        }
    }

}
