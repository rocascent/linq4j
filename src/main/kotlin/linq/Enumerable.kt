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

    /**
     * Applies an accumulator function over a sequence.
     * @param [func] An accumulator function to be invoked on each element.
     * @return The final accumulator value.
     * @throws [NullPointerException] source or [func] is null.
     * @throws [IllegalStateException] source contains no elements.
     */
    fun aggregate(func: (TSource, TSource) -> TSource): TSource = aggregate(this, func)

    /**
     * Applies an accumulator function over a sequence. The specified seed value is used as the initial accumulator value.
     * @param [seed] The initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @return The final accumulator value.
     * @throws [NullPointerException] source or [func] is null.
     */
    fun <TAccumulate> aggregate(seed: TAccumulate, func: (TAccumulate, TSource) -> TAccumulate): TAccumulate =
        aggregate(this, seed, func)

    /**
     * Applies an accumulator function over a sequence. The specified seed value is used as the initial accumulator value, and the specified function is used to select the result value.
     * @param [seed] The initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @param [resultSelector] A function to transform the final accumulator value into the result value.
     * @return The transformed final accumulator value.
     * @throws [NullPointerException] source or [func] or [resultSelector] is null.
     */
    fun <TAccumulate, TResult> aggregate(
        seed: TAccumulate,
        func: (TAccumulate, TSource) -> TAccumulate,
        resultSelector: (TAccumulate) -> TResult
    ): TResult = aggregate(this, seed, func, resultSelector)

    /**
     * Applies an accumulator function over a sequence, grouping results by key.
     * @param [keySelector] A function to extract the key for each element.
     * @param [seed] The initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @param [keyComparer] An [EqualityComparer] to compare keys with.
     * @return An enumerable containing the aggregates corresponding to each key deriving from source.
     */
    fun <TKey, TAccumulate> aggregateBy(
        keySelector: (TSource) -> TKey,
        seed: TAccumulate,
        func: (TAccumulate, TSource) -> TAccumulate,
        keyComparer: EqualityComparer<TKey>? = null
    ): Enumerable<Map.Entry<TKey, TAccumulate>> = aggregateBy(this, keySelector, seed, func, keyComparer)


    /**
     * Applies an accumulator function over a sequence, grouping results by key.
     * @param [keySelector] A function to extract the key for each element.
     * @param [seedSelector] A factory for the initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @param [keyComparer] An [EqualityComparer] to compare keys with.
     * @return An enumerable containing the aggregates corresponding to each key deriving from source.
     */
    fun <TKey, TAccumulate> aggregateBy(
        keySelector: (TSource) -> TKey,
        seedSelector: (TKey) -> TAccumulate,
        func: (TAccumulate, TSource) -> TAccumulate,
        keyComparer: EqualityComparer<TKey>? = null
    ): Enumerable<Map.Entry<TKey, TAccumulate>> = aggregateBy(this, keySelector, seedSelector, func, keyComparer)

    /**
     * Determines whether a sequence contains any elements.
     * @return true if the source sequence contains any elements; otherwise, false.
     * @throws NullPointerException source is null.
     */
    fun any(): Boolean = any(this)

    /**
     * Determines whether any element of a sequence satisfies a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return true if the source sequence is not empty and at least one of its elements passes the test in the specified predicate; otherwise, false.
     * @throws NullPointerException source or [predicate] is null.
     */
    fun any(predicate: (TSource) -> Boolean): Boolean = any(this, predicate)

    /**
     * Determines whether all elements of a sequence satisfy a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return true if every element of the source sequence passes the test in the specified predicate, or if the sequence is empty; otherwise, false.
     * @throws NullPointerException source or [predicate] is null.
     */
    fun all(predicate: (TSource) -> Boolean): Boolean = all(this, predicate)

    /**
     * Computes the average of a sequence of [Integer] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] source or [selector] is null.
     * @throws [IllegalStateException] source contains no elements.
     */
    fun averageInt(selector: (TSource) -> Int) = averageInt(this, selector)

    /**
     * Computes the average of a sequence of [Long] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] source or [selector] is null.
     * @throws [IllegalStateException] source contains no elements.
     */
    fun averageLong(selector: (TSource) -> Long) = averageLong(this, selector)

    /**
     * Computes the average of a sequence of [Float] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] source or [selector] is null.
     * @throws [IllegalStateException] source contains no elements.
     */
    fun averageFloat(selector: (TSource) -> Float) = averageFloat(this, selector)

    /**
     * Computes the average of a sequence of [Double] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] source or [selector] is null.
     * @throws [IllegalStateException] source contains no elements.
     */
    fun averageDouble(selector: (TSource) -> Double) = averageDouble(this, selector)

    /**
     * Computes the average of a sequence of [BigDecimal] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] source or [selector] is null.
     * @throws [IllegalStateException] source contains no elements.
     */
    fun averageBigDecimal(selector: (TSource) -> BigDecimal) = averageBigDecimal(this, selector)

    /**
     * Casts the elements of an [Enumerable] to the specified type.
     * @param [clazz] the type class.
     * @return An [Enumerable] that contains each element of the source sequence cast to the specified type.
     * @throws [NullPointerException] source is null.
     * @throws [ClassCastException] An element in the sequence cannot be cast to type TResult.
     */
    fun <TResult> cast(clazz: Class<TResult>) = cast(this, clazz)

    /**
     * Splits the elements of a sequence into chunks of size at most size.
     * @param [size] The maximum size of each chunk.
     * @return An [Enumerable] that contains the elements the input sequence split into chunks of size [size].
     * @throws [NullPointerException] source is null.
     * @throws [IllegalArgumentException] [size] is below 1.
     */
    fun chunk(size: Int) = chunk(this, size)

    /**
     * Concatenates two sequences.
     * @param [other] The sequence to concatenate to the source.
     * @return An [Enumerable] that contains the concatenated elements of the two input sequences.
     * @throws [NullPointerException] source or [other] is null.
     */
    fun concat(other: Iterable<TSource>) = concat(this, other)

    /**
     * Determines whether a sequence contains a specified element by using the default equality comparer.
     * @param [value] The value to locate in the sequence.
     * @return true if the source sequence contains an element that has the specified value; otherwise, false.
     * @throws [NullPointerException] source is null.
     */
    fun contains(value: TSource): Boolean = contains(this, value)

    /**
     * Determines whether a sequence contains a specified element by using a specified [EqualityComparer].
     * @param [value] The value to locate in the sequence.
     * @param [comparer] An equality comparer to compare values.
     * @return true if the source sequence contains an element that has the specified value; otherwise, false.
     * @throws [NullPointerException] source is null.
     */
    fun contains(value: TSource, comparer: EqualityComparer<TSource>): Boolean = contains(this, value, comparer)

    /**
     * Returns the number of elements in a sequence.
     * @return The number of elements in the input sequence.
     * @throws [NullPointerException] source is null.
     */
    fun count(): Int = count(this)

    /**
     * Returns a number that represents how many elements in the specified sequence satisfy a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return A number that represents how many elements in the sequence satisfy the condition in the predicate function.
     * @throws [NullPointerException] source or predicate is null.
     */
    fun count(predicate: (TSource) -> Boolean): Int = count(this, predicate)

    /**
     * Returns a [Long] that represents the total number of elements in a sequence.
     * @return The number of elements in the input sequence.
     * @throws [NullPointerException] source is null.
     */
    fun longCount(): Long = longCount(this)

    /**
     * Returns a [Long] that represents how many elements in a sequence satisfy a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return A number that represents how many elements in the sequence satisfy the condition in the predicate function.
     * @throws [NullPointerException] source or predicate is null.
     */
    fun longCount(predicate: (TSource) -> Boolean): Long = longCount(this, predicate)

    /**
     * Returns the element at a specified index in a sequence.
     * @param [index] The zero-based index of the element to retrieve.
     * @return The element at the specified position in the source sequence.
     * @throws [NullPointerException] source is null.
     * @throws [IllegalArgumentException] index is less than 0 or greater than or equal to the number of elements in source.
     */
    fun elementAt(index: Int): TSource = elementAt(this, index)

    /**
     * Returns the element at a specified index in a sequence or null if the index is out of range.
     * @param [index] The zero-based index of the element to retrieve.
     * @return null if the index is outside the bounds of the source sequence; otherwise, the element at the specified position in the source sequence.
     * @throws [NullPointerException] source is null.
     */
    fun elementAtOrDefault(index: Int): TSource? = elementAtOrDefault(this, index)

    /**
     * Returns the first element of a sequence.
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

    fun sumInt(selector: (TSource) -> Int): Int = sumInt(this, selector)

    fun sumLong(selector: (TSource) -> Long): Long = sumLong(this, selector)

    fun sumFloat(selector: (TSource) -> Float): Float = sumFloat(this, selector)

    fun sumDouble(selector: (TSource) -> Double): Double = sumDouble(this, selector)

    fun sumBigDecimal(selector: (TSource) -> BigDecimal): BigDecimal = sumBigDecimal(this, selector)


    fun take(count: Int): Enumerable<TSource> = take(this, count)

    fun takeRange(startInclusive: Int, endExclusive: Int): Enumerable<TSource> {
        null!!
    }

    fun where(predicate: (TSource) -> Boolean): Enumerable<TSource> = where(this, predicate)

    fun where(predicate: (TSource, Int) -> Boolean): Enumerable<TSource> = where(this, predicate)

    fun toList(): List<TSource> = toList(this)
}
