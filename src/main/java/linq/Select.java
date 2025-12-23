package linq;

import linq.collections.enumerable.ArrayEnumerable;
import linq.collections.enumerable.EmptyEnumerable;
import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Select {
    static <TSource, TResult> Enumerable<TResult> select(Enumerable<TSource> source, Function<TSource, TResult> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }
        if (selector == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.selector);
        }

        if (source instanceof AbstractIterator<TSource> iterator) {
            return iterator.select(selector);
        }

        if (source instanceof ArrayEnumerable<TSource> array) {
            if (array.count() == 0) return new EmptyEnumerable<>();
            return new ArraySelectIterator<>(array, selector);
        }

        return new EnumerableSelectIterator<>(source, selector);
    }

    static <TSource, TResult> Enumerable<TResult> select(Enumerable<TSource> source, BiFunction<TSource, Integer, TResult> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (selector == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.selector);
        }

        if (Enumerable.isEmptyArray(source)) {
            return Enumerable.empty();
        }

        return new Enumerable<>() {
            @Override
            public Enumerator<TResult> enumerator() {
                return new EnumerableIndexSelectIterator<>(source.enumerator(), selector);
            }
        };
    }

    static final class ArraySelectIterator<TSource, TResult> extends AbstractIterator<TResult> {
        private final ArrayEnumerable<TSource> source;
        private final Function<TSource, TResult> selector;

        public ArraySelectIterator(ArrayEnumerable<TSource> source, Function<TSource, TResult> selector) {
            assert (source != null);
            assert (selector != null);
            assert (source.count() > 0);
            this.source = source;
            this.selector = selector;
        }

        @Override
        protected AbstractIterator<TResult> getClone() {
            return new ArraySelectIterator<>(source, selector);
        }

        @Override
        public boolean moveNext() {
            var source = this.source;
            var index = state - 1;
            if (index < source.count()) {
                state++;
                current = selector.apply(source.get(index));
                return true;
            }

            close();
            return false;
        }

        @Override
        public <TResult2> Enumerable<TResult2> select(Function<TResult, TResult2> selector) {
            return new ArraySelectIterator<>(source, this.selector.andThen(selector));
        }

        @Override
        public List<TResult> toList() {
            var count = source.count();
            var results = new ArrayList<TResult>(count);
            for (var i = 0; i < count; i++) {
                results.add(selector.apply(source.get(i)));
            }
            return results;
        }

        @Override
        public int getCount() {
            for (var item : source) {
                selector.apply(item);
            }
            return source.count();
        }

        @Override
        public AbstractIterator<TResult> skip(int count) {
            assert (count > 0);
            if (count >= source.count()) {
                return null;
            }
            return new ArraySkipTakeSelectIterator<>(source, selector, count, Integer.MAX_VALUE);
        }

        @Override
        public TResult tryGetFirst(boolean[] found) {
            assert (source.count() > 0);
            found[0] = true;
            return selector.apply(source.get(0));
        }
    }

    static final class EnumerableSelectIterator<TSource, TResult> extends AbstractIterator<TResult> {
        private final Enumerable<TSource> source;
        private final Function<TSource, TResult> selector;
        private Enumerator<TSource> enumerator;

        public EnumerableSelectIterator(Enumerable<TSource> source, Function<TSource, TResult> selector) {
            assert (source != null);
            assert (selector != null);
            this.source = source;
            this.selector = selector;
        }

        @Override
        protected AbstractIterator<TResult> getClone() {
            return new EnumerableSelectIterator<>(source, selector);
        }

        @Override
        public void close() {
            if (enumerator != null) {
                enumerator.close();
                enumerator = null;
            }
            super.close();
        }

        @Override
        public boolean moveNext() {
            switch (state) {
                case 1:
                    enumerator = source.enumerator();
                    state = 2;
                case 2:
                    assert (enumerator != null);
                    if (enumerator.moveNext()) {
                        current = selector.apply(enumerator.current());
                        return true;
                    }

                    close();
                    break;
            }

            return false;
        }


        @Override
        public <TResult2> Enumerable<TResult2> select(Function<TResult, TResult2> selector) {
            return new EnumerableSelectIterator<>(source, this.selector.andThen(selector));
        }

        @Override
        public List<TResult> toList() {
            var results = new ArrayList<TResult>();
            for (var item : this) {
                results.add(item);
            }
            return results;
        }

        @Override
        public int getCount() {
            int count = 0;

            for (var item : source) {
                selector.apply(item);
                count++;
            }

            return count;
        }

        @Override
        public TResult tryGetFirst(boolean[] found) {
            try (var e = source.enumerator()) {
                if (e.moveNext()) {
                    found[0] = true;
                    return selector.apply(e.current());
                }
                found[0] = false;
                return null;
            }
        }
    }

    static final class EnumerableIndexSelectIterator<TSource, TResult> implements Enumerator<TResult> {
        private final Enumerator<TSource> enumerator;
        private final BiFunction<TSource, Integer, TResult> selector;
        private int index = -1;
        private TResult current = null;

        public EnumerableIndexSelectIterator(Enumerator<TSource> enumerator, BiFunction<TSource, Integer, TResult> selector) {
            assert (enumerator != null);
            assert (selector != null);
            this.enumerator = enumerator;
            this.selector = selector;
        }


        @Override
        public void close() {
            enumerator.close();
            current = null;
        }

        @Override
        public boolean moveNext() {
            if (enumerator.moveNext()) {
                index++;
                current = selector.apply(enumerator.current(), index);
                return true;
            }

            close();
            return false;
        }

        @Override
        public TResult current() {
            return current;
        }
    }

    static final class ArraySkipTakeSelectIterator<TSource, TResult> extends AbstractIterator<TResult> {
        private final ArrayEnumerable<TSource> source;
        private final Function<TSource, TResult> selector;
        private final int minIndexInclusive;
        private final int maxIndexInclusive;

        public ArraySkipTakeSelectIterator(ArrayEnumerable<TSource> source, Function<TSource, TResult> selector, int minIndexInclusive, int maxIndexInclusive) {
            assert (source != null);
            assert (selector != null);
            assert (minIndexInclusive >= 0);
            assert (minIndexInclusive <= maxIndexInclusive);
            this.source = source;
            this.selector = selector;
            this.minIndexInclusive = minIndexInclusive;
            this.maxIndexInclusive = maxIndexInclusive;
        }

        @Override
        protected AbstractIterator<TResult> getClone() {
            return new ArraySkipTakeSelectIterator<>(source, selector, minIndexInclusive, maxIndexInclusive);
        }

        @Override
        public boolean moveNext() {
            var index = state - 1;
            if (index <= maxIndexInclusive - minIndexInclusive && index < source.count() - minIndexInclusive) {
                current = selector.apply(source.get(minIndexInclusive + index));
                ++state;
                return true;
            }

            close();
            return false;
        }

        @Override
        public <TResult2> Enumerable<TResult2> select(Function<TResult, TResult2> selector) {
            return new ArraySkipTakeSelectIterator<>(source, this.selector.andThen(selector), minIndexInclusive, maxIndexInclusive);
        }

        @Override
        public AbstractIterator<TResult> skip(int count) {
            assert (count > 0);
            var minIndex = minIndexInclusive + count;
            return minIndex > maxIndexInclusive ? null : new ArraySkipTakeSelectIterator<>(source, selector, minIndex, maxIndexInclusive);
        }

        @Override
        public List<TResult> toList() {
            int count = count();
            if (count == 0) {
                return new ArrayList<>();
            }

            var sourceIndex = minIndexInclusive;
            var list = new ArrayList<TResult>(count);
            for (var i = 0; i < count; i++, sourceIndex++) {
                list.add(selector.apply(source.get(sourceIndex)));
            }

            return list;
        }

        @Override
        public int getCount() {
            var count = count();

            int end = minIndexInclusive + count;
            for (int i = minIndexInclusive; i != end; ++i) {
                selector.apply(source.get(i));
            }

            return count;
        }
    }
}