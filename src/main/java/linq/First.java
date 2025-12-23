package linq;

import linq.collections.enumerable.ArrayEnumerable;
import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

import java.util.function.Predicate;

public class First {
    static <TSource> TSource first(Enumerable<TSource> source) {
        var found = new boolean[1];
        var first = tryGetFirst(source, found);
        if (!found[0]) {
            ThrowHelper.throwNoElementsException();
        }
        return first;
    }

    static <TSource> TSource first(Enumerable<TSource> source, Predicate<TSource> predicate) {
        var found = new boolean[1];
        var first = tryGetFirst(source, predicate, found);
        if (!found[0]) {
            ThrowHelper.throwNoMatchException();
        }
        return first;
    }

    static <TSource> TSource firstOrDefault(Enumerable<TSource> source) {
        return tryGetFirst(source, new boolean[1]);
    }

    static <TSource> TSource firstOrDefault(Enumerable<TSource> source, TSource defaultValue) {
        var found = new boolean[1];
        var first = tryGetFirst(source, found);
        return found[0] ? first : defaultValue;
    }

    static <TSource> TSource firstOrDefault(Enumerable<TSource> source, Predicate<TSource> predicate) {
        return tryGetFirst(source, predicate, new boolean[1]);
    }

    static <TSource> TSource firstOrDefault(Enumerable<TSource> source, Predicate<TSource> predicate, TSource defaultValue) {
        var found = new boolean[1];
        var first = tryGetFirst(source, predicate, found);
        return found[0] ? first : defaultValue;
    }

    private static <TSource> TSource tryGetFirst(Enumerable<TSource> source, boolean[] found) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }
        if (source instanceof AbstractIterator<TSource> iterator) {
            return iterator.tryGetFirst(found);
        }
        return tryGetFirstNonIterator(source, found);
    }

    static <TSource> TSource tryGetFirstNonIterator(Enumerable<TSource> source, boolean[] found) {
        if (source instanceof ArrayEnumerable<TSource> array) {
            if (array.count() > 0) {
                found[0] = true;
                return array.get(0);
            }
        } else {
            try (var e = source.enumerator()) {
                if (e.moveNext()) {
                    found[0] = true;
                    return e.current();
                }
            }
        }

        found[0] = false;
        return null;
    }

    static <TSource> TSource tryGetFirst(Enumerable<TSource> source, Predicate<TSource> predicate, boolean[] found) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (predicate == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.predicate);
        }

        for (var element : source) {
            if (predicate.test(element)) {
                found[0] = true;
                return element;
            }
        }

        found[0] = false;
        return null;
    }
}
