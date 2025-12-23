package linq;

import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

import java.util.function.Predicate;

public class Count {
    static <TSource> int count(Enumerable<TSource> source) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (source instanceof AbstractIterator<TSource> iterator) {
            return iterator.getCount();
        }

        int count = 0;
        try (var e = source.enumerator()) {
            while (e.moveNext()) {
                count++;
            }
            return count;
        }
    }

    static <TSource> int count(Enumerable<TSource> source, Predicate<TSource> predicate) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (predicate == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.predicate);
        }

        int count = 0;
        for (var item : source) {
            if (predicate.test(item)) {
                count++;
            }
        }
        return count;
    }

    static <TSource> long longCount(Enumerable<TSource> source) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        long count = 0;
        try (var e = source.enumerator()) {
            while (e.moveNext()) {
                count++;
            }
            return count;
        }
    }

    static <TSource> long longCount(Enumerable<TSource> source, Predicate<TSource> predicate) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (predicate == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.predicate);
        }

        long count = 0;
        for (var item : source) {
            if (predicate.test(item)) {
                count++;
            }
        }
        return count;
    }
}
