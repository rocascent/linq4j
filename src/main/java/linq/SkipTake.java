package linq;

import linq.collections.enumerable.ArrayEnumerable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SkipTake {
    static final class ArraySkipTakeIterator<TSource> extends AbstractIterator<TSource> {
        private final ArrayEnumerable<TSource> source;
        private final int minIndexInclusive;
        private final int maxIndexInclusive;

        public ArraySkipTakeIterator(ArrayEnumerable<TSource> source, int minIndexInclusive, int maxIndexInclusive) {
            assert (source != null);
            assert (minIndexInclusive >= 0);
            assert (minIndexInclusive <= maxIndexInclusive);
            this.source = source;
            this.minIndexInclusive = minIndexInclusive;
            this.maxIndexInclusive = maxIndexInclusive;
        }

        @Override
        protected AbstractIterator<TSource> getClone() {
            return new ArraySkipTakeIterator<>(source, minIndexInclusive, maxIndexInclusive);
        }

        @Override
        public boolean moveNext() {
            // _state - 1 represents the zero-based index into the list.
            // Having a separate field for the index would be more readable. However, we save it
            // into _state with a bias to minimize field size of the iterator.
            var index = state - 1;
            if (index <= maxIndexInclusive - minIndexInclusive && index < source.count() - minIndexInclusive) {
                current = source.get(minIndexInclusive + index);
                ++state;
                return true;
            }

            close();
            return false;
        }

        @Override
        public <TResult> Enumerable<TResult> select(Function<TSource, TResult> selector) {
            return new Select.ArraySkipTakeSelectIterator<>(source, selector, minIndexInclusive, maxIndexInclusive);
        }

        @Override
        public AbstractIterator<TSource> skip(int count) {
            var minIndex = minIndexInclusive + count;
            return minIndex > maxIndexInclusive ? null : new ArraySkipTakeIterator<>(source, minIndex, maxIndexInclusive);
        }

        private static int getAdjustedCount(int minIndexInclusive, int maxIndexInclusive, int sourceCount) {
            if (sourceCount <= minIndexInclusive) {
                return 0;
            }

            return Math.min(sourceCount - 1, maxIndexInclusive) - minIndexInclusive + 1;
        }

        @Override
        public int getCount() {
            return getAdjustedCount(minIndexInclusive, maxIndexInclusive, source.count());
        }

        @Override
        public List<TSource> toList() {
            var count = getCount();

            var list = new ArrayList<TSource>(count);
            if (count != 0) {
                var sourceIndex = minIndexInclusive;
                for (var i = 0; i < count; i++, sourceIndex++) {
                    list.add(source.get(sourceIndex));
                }
            }

            return list;
        }
    }


    static final class EnumerableSkipTakeIterator<TSource> extends AbstractIterator<TSource> {
        private final Enumerable<TSource> source;
        private final int minIndexInclusive;
        private final int maxIndexInclusive; // -1 if we want everything past _minIndexInclusive.
        // If this is -1, it's impossible to set a limit on the count.
        private Enumerator<TSource> enumerator;

        public EnumerableSkipTakeIterator(Enumerable<TSource> source, int minIndexInclusive, int maxIndexInclusive) {
            assert (source != null);
            assert (minIndexInclusive >= 0);
            assert (maxIndexInclusive >= -1);
            // Note that although maxIndexInclusive can't grow, it can still be int.MaxValue.
            // We support partitioning enumerables with > 2B elements. For example, e.Skip(1).Take(int.MaxValue) should work.
            // But if it is int.MaxValue, then minIndexInclusive must != 0. Otherwise, our count may overflow.
            assert (maxIndexInclusive == -1 || (maxIndexInclusive - minIndexInclusive < Integer.MAX_VALUE));
            assert (maxIndexInclusive == -1 || minIndexInclusive <= maxIndexInclusive);

            this.source = source;
            this.minIndexInclusive = minIndexInclusive;
            this.maxIndexInclusive = maxIndexInclusive;
        }

        private boolean hasLimit() {
            return maxIndexInclusive != -1;
        }

        private int limit() {
            return maxIndexInclusive + 1 - minIndexInclusive;
        }

        @Override
        protected AbstractIterator<TSource> getClone() {
            return new EnumerableSkipTakeIterator<>(source, minIndexInclusive, maxIndexInclusive);
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
        public int getCount() {
            if (!hasLimit()) {
                return Math.max(source.count() - minIndexInclusive, 0);
            }

            try (var en = source.enumerator()) {
                var count = skipAndCount(maxIndexInclusive + 1, en);
                return Math.max(count - minIndexInclusive, 0);
            }
        }

        @Override
        public boolean moveNext() {
            int taken = state - 3;
            if (taken < -2) {
                close();
                return false;
            }

            switch (state) {
                case 1:
                    enumerator = source.enumerator();
                    state = 2;
                case 2:
                    assert (enumerator != null);
                    if (!skipBeforeFirst(enumerator)) {
                        // Reached the end before we finished skipping.
                        break;
                    }

                    state = 3;
                default:
                    assert (enumerator != null);
                    if ((!hasLimit() || taken < limit()) && enumerator.moveNext()) {
                        if (hasLimit()) {
                            // If we are taking an unknown number of elements, it's important not to increment _state.
                            // _state - 3 may eventually end up overflowing & we'll hit the Dispose branch even though
                            // we haven't finished enumerating.
                            state++;
                        }
                        current = enumerator.current();
                        return true;
                    }

                    break;
            }

            close();
            return false;
        }

        @Override
        public List<TSource> toList() {
            try (var en = source.enumerator()) {
                if (skipBeforeFirst(en) && en.moveNext()) {
                    int remaining = limit() - 1; // Max number of items left, not counting the current element.
                    int comparand = hasLimit() ? 0 : Integer.MIN_VALUE; // If we don't have an upper bound, have the comparison always return true.

                    var result = new ArrayList<TSource>();
                    do {
                        remaining--;
                        result.add(en.current());
                    }
                    while (remaining >= comparand && en.moveNext());

                    return result;
                }
            }

            return new ArrayList<>();
        }

        private boolean skipBeforeFirst(Enumerator<TSource> en) {
            return skipBefore(minIndexInclusive, en);
        }

        private static <TSource> boolean skipBefore(int index, Enumerator<TSource> en) {
            return skipAndCount(index, en) == index;
        }

        private static <TSource> int skipAndCount(int index, Enumerator<TSource> en) {
            assert (en != null);

            for (int i = 0; i < index; i++) {
                if (!en.moveNext()) {
                    return i;
                }
            }

            return index;
        }
    }
}
