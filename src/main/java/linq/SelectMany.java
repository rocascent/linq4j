package linq;

import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SelectMany {
    static <TSource, TResult> Enumerable<TResult> selectMany(Enumerable<TSource> source, Function<TSource, Iterable<TResult>> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (selector == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.selector);
        }

        if (Enumerable.isEmptyArray(source)) {
            return Enumerable.empty();
        }
        return new SelectManySingleSelectorIterator<>(source, selector);
    }

    static <TSource, TResult> Enumerable<TResult> selectMany(Enumerable<TSource> source, BiFunction<TSource, Integer, Iterable<TResult>> selector) {
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
                return new SelectManyIndexSelectorIterator<>(source.enumerator(), selector);
            }
        };
    }

    static <TSource, TCollection, TResult> Enumerable<TResult> selectMany(Enumerable<TSource> source, BiFunction<TSource, Integer, Iterable<TCollection>> collectionSelector, BiFunction<TSource, TCollection, TResult> resultSelector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (collectionSelector == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.collectionSelector);
        }

        if (resultSelector == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.resultSelector);
        }

        if (Enumerable.isEmptyArray(source)) {
            return Enumerable.empty();
        }

        return new Enumerable<>() {
            @Override
            public Enumerator<TResult> enumerator() {
                return new SelectManyIndexMapperIterator<>(source.enumerator(), collectionSelector, resultSelector);
            }
        };
    }

    static <TSource, TCollection, TResult> Enumerable<TResult> selectMany(Enumerable<TSource> source, Function<TSource, Iterable<TCollection>> collectionSelector, BiFunction<TSource, TCollection, TResult> resultSelector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if (collectionSelector == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.collectionSelector);
        }

        if (resultSelector == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.resultSelector);
        }

        if (Enumerable.isEmptyArray(source)) {
            return Enumerable.empty();
        }

        return new Enumerable<>() {
            @Override
            public Enumerator<TResult> enumerator() {
                return new SelectManySingleMapperIterator<>(source.enumerator(), collectionSelector, resultSelector);
            }
        };
    }

    static class SelectManySingleSelectorIterator<TSource, TResult> extends AbstractIterator<TResult> {
        private final Enumerable<TSource> source;
        private final Function<TSource, Iterable<TResult>> selector;
        private Enumerator<TSource> sourceEnumerator;
        private Enumerator<TResult> subEnumerator;

        SelectManySingleSelectorIterator(Enumerable<TSource> source, Function<TSource, Iterable<TResult>> selector) {
            assert (source != null);
            assert (selector != null);
            this.source = source;
            this.selector = selector;
        }

        @Override
        protected AbstractIterator<TResult> getClone() {
            return new SelectManySingleSelectorIterator<>(source, selector);
        }

        @Override
        public void close() {
            if (subEnumerator != null) {
                subEnumerator.close();
                subEnumerator = null;
            }

            if (sourceEnumerator != null) {
                sourceEnumerator.close();
                sourceEnumerator = null;
            }

            super.close();
        }

        @Override
        public boolean moveNext() {
            loop:
            while (true) {
                switch (state) {
                    case 1:
                        // Retrieve the source enumerator.
                        sourceEnumerator = source.enumerator();
                        state = 2;
                        break;
                    case 2:
                        // Take the next element from the source enumerator.
                        assert (sourceEnumerator != null);
                        if (!sourceEnumerator.moveNext()) {
                            break loop;
                        }

                        var element = sourceEnumerator.current();

                        // Project it into a sub-collection and get its enumerator.
                        subEnumerator = Enumerable.of(selector.apply(element)).enumerator();
                        state = 3;
                        break;
                    case 3:
                        // Take the next element from the sub-collection and yield.
                        assert (subEnumerator != null);
                        if (!subEnumerator.moveNext()) {
                            subEnumerator.close();
                            subEnumerator = null;
                            state = 2;
                            break;
                        }

                        current = subEnumerator.current();
                        return true;
                    default:
                        break loop;
                }
            }
            close();
            return false;
        }

        @Override
        public List<TResult> toList() {
            var results = new ArrayList<TResult>();
            for (var item : source) {
                selector.apply(item).iterator().forEachRemaining(results::add);
            }
            return results;
        }

        @Override
        public int getCount() {
            int count = 0;

            for (var element : source) {
                count += Count.count(Enumerable.of(selector.apply(element)));
            }

            return count;
        }
    }

    static class SelectManyIndexSelectorIterator<TSource, TResult> implements Enumerator<TResult> {
        private final Enumerator<TSource> enumerator;
        private final BiFunction<TSource, Integer, Iterable<TResult>> selector;
        private Enumerator<TResult> subEnumerator;
        private int index = -1;
        private TResult current = null;

        public SelectManyIndexSelectorIterator(Enumerator<TSource> enumerator, BiFunction<TSource, Integer, Iterable<TResult>> selector) {
            assert (enumerator != null);
            assert (selector != null);
            this.enumerator = enumerator;
            this.selector = selector;
        }

        @Override
        public void close() {
            if (subEnumerator != null) {
                subEnumerator.close();
                subEnumerator = null;
            }
            enumerator.close();
            current = null;
        }

        @Override
        public boolean moveNext() {
            while (true) {
                if (subEnumerator == null) {
                    if (!enumerator.moveNext()) {
                        break;
                    }

                    var element = enumerator.current();
                    index++;

                    subEnumerator = Enumerable.of(selector.apply(element, index)).enumerator();
                } else {
                    if (!subEnumerator.moveNext()) {
                        subEnumerator.close();
                        subEnumerator = null;
                        continue;
                    }

                    current = subEnumerator.current();
                    return true;
                }
            }

            close();
            return false;
        }

        @Override
        public TResult current() {
            return current;
        }
    }

    static class SelectManySingleMapperIterator<TSource, TCollection, TResult> implements Enumerator<TResult> {
        private final Enumerator<TSource> enumerator;
        private final Function<TSource, Iterable<TCollection>> collectionSelector;
        private final BiFunction<TSource, TCollection, TResult> resultSelector;
        private Enumerator<TCollection> subEnumerator;
        private TResult current = null;

        SelectManySingleMapperIterator(Enumerator<TSource> enumerator, Function<TSource, Iterable<TCollection>> collectionSelector, BiFunction<TSource, TCollection, TResult> resultSelector) {
            assert (enumerator != null);
            assert (collectionSelector != null);
            assert (resultSelector != null);
            this.enumerator = enumerator;
            this.collectionSelector = collectionSelector;
            this.resultSelector = resultSelector;
        }

        @Override
        public void close() {
            if (subEnumerator != null) {
                subEnumerator.close();
                subEnumerator = null;
            }
            enumerator.close();
            current = null;
        }

        @Override
        public boolean moveNext() {
            while (true) {
                if (subEnumerator == null) {
                    if (!enumerator.moveNext()) {
                        break;
                    }

                    var element = enumerator.current();

                    subEnumerator = Enumerable.of(collectionSelector.apply(element)).enumerator();
                } else {
                    if (!subEnumerator.moveNext()) {
                        subEnumerator.close();
                        subEnumerator = null;
                        continue;
                    }

                    current = resultSelector.apply(enumerator.current(), subEnumerator.current());
                    return true;
                }
            }

            close();
            return false;
        }

        @Override
        public TResult current() {
            return current;
        }
    }

    static class SelectManyIndexMapperIterator<TSource, TCollection, TResult> implements Enumerator<TResult> {
        private final Enumerator<TSource> enumerator;
        private final BiFunction<TSource, Integer, Iterable<TCollection>> collectionSelector;
        private final BiFunction<TSource, TCollection, TResult> resultSelector;
        private Enumerator<TCollection> subEnumerator;
        private int index = -1;
        private TResult current = null;

        SelectManyIndexMapperIterator(Enumerator<TSource> enumerator, BiFunction<TSource, Integer, Iterable<TCollection>> collectionSelector, BiFunction<TSource, TCollection, TResult> resultSelector) {
            assert (enumerator != null);
            assert (collectionSelector != null);
            assert (resultSelector != null);
            this.enumerator = enumerator;
            this.collectionSelector = collectionSelector;
            this.resultSelector = resultSelector;
        }

        @Override
        public void close() {
            if (subEnumerator != null) {
                subEnumerator.close();
                subEnumerator = null;
            }
            enumerator.close();
            current = null;
        }

        @Override
        public boolean moveNext() {
            while (true) {
                if (subEnumerator == null) {
                    if (!enumerator.moveNext()) {
                        break;
                    }

                    var element = enumerator.current();
                    index++;

                    subEnumerator = Enumerable.of(collectionSelector.apply(element, index)).enumerator();
                } else {
                    if (!subEnumerator.moveNext()) {
                        subEnumerator.close();
                        subEnumerator = null;
                        continue;
                    }

                    current = resultSelector.apply(enumerator.current(), subEnumerator.current());
                    return true;
                }
            }

            close();
            return false;
        }

        @Override
        public TResult current() {
            return current;
        }
    }
}
