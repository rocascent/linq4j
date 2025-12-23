package linq;

import linq.collections.IteratorAdapter;
import linq.collections.enumerable.ArrayEnumerable;
import linq.collections.enumerable.EmptyEnumerable;
import linq.collections.enumerable.IterableEnumerable;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Enumerable<TSource> implements Iterable<TSource> {
    public abstract Enumerator<TSource> enumerator();

    @Override
    public Iterator<TSource> iterator() {
        return new IteratorAdapter<>(enumerator());
    }

    public static <TSource> Enumerable<TSource> empty() {
        return new EmptyEnumerable<>();
    }

    public static <TSource> boolean isEmptyArray(Iterable<TSource> source) {
        return source instanceof ArrayEnumerable<TSource> array && array.count() == 0;
    }

    @SafeVarargs
    public static <T> Enumerable<T> of(T... source) {
        return source == null ? empty() : new ArrayEnumerable<>(source);
    }

    public static <T> Enumerable<T> of(Iterable<T> source) {
        return source == null ? empty() : new IterableEnumerable<>(source);
    }


    public int count() {
        return Count.count(this);
    }

    public int count(Predicate<TSource> predicate) {
        return Count.count(this, predicate);
    }

    public long longCount() {
        return Count.longCount(this);
    }

    public long longCount(Predicate<TSource> predicate) {
        return Count.longCount(this, predicate);
    }

    /**
     * Returns the first element of a sequence.
     *
     * @return The first element in the specified sequence.
     * @throws NullPointerException  source is null.
     * @throws IllegalStateException The source sequence is empty.
     */
    public TSource first() {
        return First.first(this);
    }

    /**
     * Returns the first element of a sequence.
     *
     * @param predicate A function to test each element for a condition.
     * @return The first element in the sequence that passes the test in the specified predicate function.
     * @throws NullPointerException  source or predicate is null.
     * @throws IllegalStateException No element satisfies the condition in predicate. -or- The source sequence is empty.
     */
    public TSource first(Predicate<TSource> predicate) {
        return First.first(this, predicate);
    }

    /**
     * Returns the first element of a sequence, or a default value if the sequence contains no elements.
     *
     * @return null if source is empty; otherwise, the first element in source.
     * @throws NullPointerException source is null.
     */
    public TSource firstOrDefault() {
        return First.firstOrDefault(this);
    }

    /**
     * Returns the first element of a sequence, or a specified default value if the sequence contains no elements.
     *
     * @param defaultValue The default value to return if the sequence is empty.
     * @return {@code defaultValue} if source is empty; otherwise, the first element in source.
     * @throws NullPointerException source is null.
     */
    public TSource firstOrDefault(TSource defaultValue) {
        return First.firstOrDefault(this, defaultValue);
    }

    /**
     * Returns the first element of the sequence that satisfies a condition or a default value if no such element is found.
     *
     * @param predicate A function to test each element for a condition.
     * @return null if source is empty or if no element passes the test specified by predicate; otherwise, the first element in source that passes the test specified by predicate.
     * @throws NullPointerException source or predicate is null.
     */
    public TSource firstOrDefault(Predicate<TSource> predicate) {
        return First.firstOrDefault(this, predicate);
    }

    /**
     * Returns the first element of the sequence that satisfies a condition or a default value if no such element is found.
     *
     * @param predicate    A function to test each element for a condition.
     * @param defaultValue The default value to return if the sequence is empty.
     * @return {@code defaultValue} if source is empty or if no element passes the test specified by predicate; otherwise, the first element in source that passes the test specified by predicate.
     * @throws NullPointerException source or predicate is null.
     */
    public TSource firstOrDefault(Predicate<TSource> predicate, TSource defaultValue) {
        return First.firstOrDefault(this, predicate, defaultValue);
    }

    /**
     *
     * @param selector
     * @param <TResult>
     * @return
     */
    public <TResult> Enumerable<TResult> select(Function<TSource, TResult> selector) {
        return Select.select(this, selector);
    }

    public <TResult> Enumerable<TResult> select(BiFunction<TSource, Integer, TResult> selector) {
        return Select.select(this, selector);
    }

    public <TResult> Enumerable<TResult> selectMany(Function<TSource, Iterable<TResult>> selector) {
        return SelectMany.selectMany(this, selector);
    }

    public <TResult> Enumerable<TResult> selectMany(BiFunction<TSource, Integer, Iterable<TResult>> selector) {
        return SelectMany.selectMany(this, selector);
    }

    public <TCollection, TResult> Enumerable<TResult> selectMany(Function<TSource, Iterable<TCollection>> collectionSelector, BiFunction<TSource, TCollection, TResult> resultSelector) {
        return SelectMany.selectMany(this, collectionSelector, resultSelector);
    }

    public <TCollection, TResult> Enumerable<TResult> selectMany(BiFunction<TSource, Integer, Iterable<TCollection>> collectionSelector, BiFunction<TSource, TCollection, TResult> resultSelector) {
        return SelectMany.selectMany(this, collectionSelector, resultSelector);
    }

    public Enumerable<TSource> skip(int count) {
        return Skip.skip(this, count);
    }

    public int sumInt(Function<TSource, Integer> selector) {
        return Sum.sumInt(this, selector);
    }

    public long sumLong(Function<TSource, Long> selector) {
        return Sum.sumLong(this, selector);
    }

    public float sumFloat(Function<TSource, Float> selector) {
        return Sum.sumFloat(this, selector);
    }

    public double sumDouble(Function<TSource, Double> selector) {
        return Sum.sumDouble(this, selector);
    }

    public BigDecimal sumBigDecimal(Function<TSource, BigDecimal> selector) {
        return Sum.sumBigDecimal(this, selector);
    }

    public Enumerable<TSource> take(int count) {
        return Take.take(this, count);
    }

    public Enumerable<TSource> where(Predicate<TSource> predicate) {
        return Where.where(this, predicate);
    }

    public Enumerable<TSource> where(BiPredicate<TSource, Integer> predicate) {
        return Where.where(this, predicate);
    }


    public List<TSource> toList() {
        return ToCollection.toList(this);
    }
}
