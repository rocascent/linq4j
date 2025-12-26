package linq

import linq.collections.IteratorAdapter
import linq.collections.enumerable.ArrayEnumerable
import linq.collections.enumerable.EmptyEnumerable
import linq.collections.enumerable.IterableEnumerable
import java.math.BigDecimal

fun <TSource> isEmptyArray(source: Iterable<TSource>): Boolean {
    return source is ArrayEnumerable && source.size == 0
}

abstract class Enumerable<TSource> : Iterable<TSource> {
    companion object {
        @JvmStatic
        fun <TSource> of(): Enumerable<TSource> {
            return EmptyEnumerable()
        }

        @JvmStatic
        fun <TSource> of(vararg source: TSource): Enumerable<TSource> {
            return ArrayEnumerable(source)
        }

        @JvmStatic
        fun <TSource> of(source: Iterable<TSource>): Enumerable<TSource> {
            return IterableEnumerable(source)
        }
    }

    abstract fun enumerator(): Enumerator<TSource>

    override fun iterator(): Iterator<TSource> {
        return IteratorAdapter(enumerator())
    }

    fun count(): Int = count(this)

    fun count(predicate: (TSource) -> Boolean): Int = count(this, predicate)

    fun longCount(): Long = longCount(this)

    fun longCount(predicate: (TSource) -> Boolean): Long = longCount(this, predicate)


    /**
     * Returns the first element of a sequence.
     *
     * @return The first element in the specified sequence.
     * @throws [NullPointerException]  source is null.
     * @throws [IllegalStateException] The source sequence is empty.
     */
    fun first(): TSource = first(this)

    /**
     * Returns the first element of a sequence.
     *
     * @param [predicate] A function to test each element for a condition.
     * @return The first element in the sequence that passes the test in the specified predicate function.
     * @throws [NullPointerException]  source or predicate is null.
     * @throws [IllegalStateException] No element satisfies the condition in predicate. -or- The source sequence is empty.
     */
    fun first(predicate: (TSource) -> Boolean): TSource = first(this, predicate)

    /**
     * Returns the first element of a sequence, or a default value if the sequence contains no elements.
     *
     * @return null if source is empty otherwise, the first element in source.
     * @throws [NullPointerException] source is null.
     */
    fun firstOrDefault(): TSource? = firstOrDefault(this)

    /**
     * Returns the first element of a sequence, or a specified default value if the sequence contains no elements.
     *
     * @param [defaultValue] The default value to return if the sequence is empty.
     * @return [defaultValue] if source is empty otherwise, the first element in source.
     * @throws [NullPointerException] source is null.
     */
    fun firstOrDefault(defaultValue: TSource): TSource = firstOrDefault(this, defaultValue)

    /**
     * Returns the first element of the sequence that satisfies a condition or a default value if no such element is found.
     *
     * @param [predicate] A function to test each element for a condition.
     * @return null if source is empty or if no element passes the test specified by predicate otherwise, the first element in source that passes the test specified by predicate.
     * @throws [NullPointerException] source or predicate is null.
     */
    fun firstOrDefault(predicate: (TSource) -> Boolean): TSource? = firstOrDefault(this, predicate)

    /**
     * Returns the first element of the sequence that satisfies a condition or a default value if no such element is found.
     *
     * @param [predicate] A function to test each element for a condition.
     * @param [defaultValue] The default value to return if the sequence is empty.
     * @return [defaultValue] if source is empty or if no element passes the test specified by predicate otherwise, the first element in source that passes the test specified by predicate.
     * @throws NullPointerException source or predicate is null.
     */
    fun firstOrDefault(predicate: (TSource) -> Boolean, defaultValue: TSource): TSource =
        firstOrDefault(this, predicate, defaultValue)

    /**
     * Projects each element of a sequence into a new form.
     * @param [selector] A transform function to apply to each element.
     * @return An Enumerable<out T> whose elements are the result of invoking the transform function on each element of source.
     * @exception [NullPointerException] source or [selector] is null.
     */
    fun <TResult> select(selector: (TSource) -> TResult): Enumerable<TResult> = select(this, selector)

    /**
     * Projects each element of a sequence into a new form by incorporating the element's index.
     * @param [selector] A transform function to apply to each source element; the second parameter of the function represents the index of the source element.
     * @return An Enumerable<out T> whose elements are the result of invoking the transform function on each element of source.
     * @exception [NullPointerException] source or [selector] is null.
     */
    fun <TResult> select(selector: (TSource, Int) -> TResult): Enumerable<TResult> = select(this, selector)

    fun <TResult> selectMany(selector: (TSource) -> Iterable<TResult>): Enumerable<TResult> =
        selectMany(this, selector)

    fun <TResult> selectMany(selector: (TSource, Int) -> Iterable<TResult>): Enumerable<TResult> =
        selectMany(this, selector)

    fun <TCollection, TResult> selectMany(
        collectionSelector: (TSource) -> Iterable<TCollection>,
        resultSelector: (TSource, TCollection) -> TResult
    ): Enumerable<TResult> = selectMany(this, collectionSelector, resultSelector)

    fun <TCollection, TResult> selectMany(
        collectionSelector: (TSource, Int) -> Iterable<TCollection>,
        resultSelector: (TSource, TCollection) -> TResult
    ): Enumerable<TResult> = selectMany(this, collectionSelector, resultSelector)


    fun skip(count: Int): Enumerable<TSource> = skip(this, count)

    fun skipWhile(predicate: (TSource) -> Boolean): Enumerable<TSource> = skipWhile(this, predicate)

    fun skipWhile(predicate: (TSource, Int) -> Boolean): Enumerable<TSource> = skipWhile(this, predicate)

    fun sumInt(selector: (TSource) -> Int): Int = sum(this, selector)

    fun sumLong(selector: (TSource) -> Long): Long = sum(this, selector)

    fun sumFloat(selector: (TSource) -> Float): Float = sum(this, selector)

    fun sumDouble(selector: (TSource) -> Double): Double = sum(this, selector)

    fun sumBigDecimal(selector: (TSource) -> BigDecimal): BigDecimal = sum(this, selector)


    fun take(count: Int): Enumerable<TSource> = take(this, count)

    fun takeRange(startInclusive: Int, endExclusive: Int): Enumerable<TSource> {
        null!!
    }

    fun where(predicate: (TSource) -> Boolean): Enumerable<TSource> = where(this, predicate)

    fun where(predicate: (TSource, Int) -> Boolean): Enumerable<TSource> = where(this, predicate)

    fun toList(): List<TSource> = toList(this)
}
