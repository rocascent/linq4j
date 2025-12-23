package linq;

import linq.collections.enumerable.ArrayEnumerable;
import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class Where {
    static <TSource> Enumerable<TSource> where(Enumerable<TSource> source, Predicate<TSource> predicate) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (predicate == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.predicate);
        }

        if (source instanceof AbstractIterator<TSource> iterator) {
            return iterator.where(predicate);
        }

        if (source instanceof ArrayEnumerable<TSource> array) {
            if (array.count() == 0) {
                return Enumerable.empty();
            }

            return new ArrayWhereIterator<>(array, predicate);
        }

        return new EnumerableWhereIterator<>(source, predicate);
    }

    static <TSource> Enumerable<TSource> where(Enumerable<TSource> source, BiPredicate<TSource, Integer> predicate) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (predicate == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.predicate);
        }

        if (Enumerable.isEmptyArray(source)) {
            return Enumerable.empty();
        }

        return new Enumerable<>() {
            @Override
            public Enumerator<TSource> enumerator() {
                return new EnumerableIndexWhereIterator<>(source.enumerator(), predicate);
            }
        };
    }

    static final class ArrayWhereIterator<TSource> extends AbstractIterator<TSource> {
        private final ArrayEnumerable<TSource> source;
        private final Predicate<TSource> predicate;

        public ArrayWhereIterator(ArrayEnumerable<TSource> source, Predicate<TSource> predicate) {
            assert (source != null && source.count() > 0);
            assert (predicate != null);
            this.source = source;
            this.predicate = predicate;
        }

        @Override
        protected AbstractIterator<TSource> getClone() {
            return new ArrayWhereIterator<>(source, predicate);
        }

        @Override
        public boolean moveNext() {
            int index = state - 1;

            while (index < source.count()) {
                var item = source.get(index);
                index = state++;
                if (predicate.test(item)) {
                    current = item;
                    return true;
                }
            }

            close();
            return false;
        }

        @Override
        public <TResult> Enumerable<TResult> select(Function<TSource, TResult> selector) {
            return new ArrayWhereSelectIterator<>(source, predicate, selector);
        }

        @Override
        public Enumerable<TSource> where(Predicate<TSource> predicate) {
            return new ArrayWhereIterator<>(source, this.predicate.and(predicate));
        }

        @Override
        public List<TSource> toList() {
            var results = new ArrayList<TSource>();
            for (var item : source) {
                if (this.predicate.test(item)) {
                    results.add(item);
                }
            }
            return results;
        }

        @Override
        public int getCount() {
            int count = 0;

            for (var item : source) {
                if (predicate.test(item)) {
                    count++;
                }
            }

            return count;
        }
    }

    static final class EnumerableWhereIterator<TSource> extends AbstractIterator<TSource> {
        private final Enumerable<TSource> source;
        private final Predicate<TSource> predicate;
        private Enumerator<TSource> enumerator;

        public EnumerableWhereIterator(Enumerable<TSource> source, Predicate<TSource> predicate) {
            assert (source != null);
            assert (predicate != null);
            this.source = source;
            this.predicate = predicate;
        }

        @Override
        protected AbstractIterator<TSource> getClone() {
            return new EnumerableWhereIterator<>(source, predicate);
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
                    while (enumerator.moveNext()) {
                        var item = enumerator.current();
                        if (predicate.test(item)) {
                            current = item;
                            return true;
                        }
                    }

                    close();
                    break;
            }

            return false;
        }

        @Override
        public <TResult> Enumerable<TResult> select(Function<TSource, TResult> selector) {
            return new EnumerableWhereSelectIterator<>(source, predicate, selector);
        }

        @Override
        public Enumerable<TSource> where(Predicate<TSource> predicate) {
            return new EnumerableWhereIterator<>(source, this.predicate.and(predicate));
        }

        @Override
        public List<TSource> toList() {
            var results = new ArrayList<TSource>();
            for (var item : source) {
                if (this.predicate.test(item)) {
                    results.add(item);
                }
            }
            return results;
        }

        @Override
        public int getCount() {
            int count = 0;

            for (var item : source) {
                if (predicate.test(item)) {
                    count++;
                }
            }

            return count;
        }
    }

    static final class ArrayWhereSelectIterator<TSource, TResult> extends AbstractIterator<TResult> {
        private final ArrayEnumerable<TSource> source;
        private final Predicate<TSource> predicate;
        private final Function<TSource, TResult> selector;

        public ArrayWhereSelectIterator(ArrayEnumerable<TSource> source, Predicate<TSource> predicate, Function<TSource, TResult> selector) {
            assert (source != null && source.count() > 0);
            assert (predicate != null);
            assert (selector != null);
            this.source = source;
            this.predicate = predicate;
            this.selector = selector;
        }

        @Override
        protected AbstractIterator<TResult> getClone() {
            return new ArrayWhereSelectIterator<>(source, predicate, selector);
        }

        @Override
        public boolean moveNext() {
            int index = state - 1;

            while (index < source.count()) {
                var item = source.get(index);
                index = state++;
                if (predicate.test(item)) {
                    current = selector.apply(item);
                    return true;
                }
            }

            close();
            return false;
        }

        @Override
        public <TResult2> Enumerable<TResult2> select(Function<TResult, TResult2> selector) {
            return new ArrayWhereSelectIterator<>(source, predicate, this.selector.andThen(selector));
        }

        @Override
        public List<TResult> toList() {
            var results = new ArrayList<TResult>();
            for (var item : source) {
                if (this.predicate.test(item)) {
                    results.add(this.selector.apply(item));
                }
            }
            return results;
        }

        @Override
        public int getCount() {
            int count = 0;

            for (var item : source) {
                if (predicate.test(item)) {
                    selector.apply(item);
                    count++;
                }
            }

            return count;
        }
    }

    static final class EnumerableWhereSelectIterator<TSource, TResult> extends AbstractIterator<TResult> {
        private final Enumerable<TSource> source;
        private final Predicate<TSource> predicate;
        private final Function<TSource, TResult> selector;
        private Enumerator<TSource> enumerator;

        public EnumerableWhereSelectIterator(Enumerable<TSource> source, Predicate<TSource> predicate, Function<TSource, TResult> selector) {
            assert (source != null);
            assert (predicate != null);
            assert (selector != null);
            this.source = source;
            this.predicate = predicate;
            this.selector = selector;
        }

        @Override
        protected AbstractIterator<TResult> getClone() {
            return new EnumerableWhereSelectIterator<>(source, predicate, selector);
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
                    while (enumerator.moveNext()) {
                        var item = enumerator.current();
                        if (predicate.test(item)) {
                            current = selector.apply(item);
                            return true;
                        }
                    }

                    close();
                    break;
            }

            return false;
        }

        @Override
        public <TResult2> Enumerable<TResult2> select(Function<TResult, TResult2> selector) {
            return new EnumerableWhereSelectIterator<>(source, predicate, this.selector.andThen(selector));
        }

        @Override
        public List<TResult> toList() {
            var results = new ArrayList<TResult>();
            for (var item : source) {
                if (this.predicate.test(item)) {
                    results.add(this.selector.apply(item));
                }
            }
            return results;
        }

        @Override
        public int getCount() {
            int count = 0;

            for (var item : source) {
                if (predicate.test(item)) {
                    selector.apply(item);
                    count++;
                }
            }

            return count;
        }
    }

    static final class EnumerableIndexWhereIterator<TSource> implements Enumerator<TSource> {
        private final Enumerator<TSource> enumerator;
        private final BiPredicate<TSource, Integer> predicate;
        private int index = -1;
        private TSource current = null;

        EnumerableIndexWhereIterator(Enumerator<TSource> enumerator, BiPredicate<TSource, Integer> predicate) {
            assert (enumerator != null);
            assert (predicate != null);
            this.enumerator = enumerator;
            this.predicate = predicate;
        }


        @Override
        public boolean moveNext() {
            while (enumerator.moveNext()) {
                var item = enumerator.current();
                index++;
                if (predicate.test(item, index)) {
                    current = item;
                    return true;
                }
            }

            close();
            return false;
        }

        @Override
        public TSource current() {
            return current;
        }

        @Override
        public void close() {
            enumerator.close();
            current = null;
        }
    }
}
