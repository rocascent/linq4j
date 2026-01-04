package linq

import java.math.BigDecimal

class Enumerable<TSource> internal constructor(private val source: Sequence<TSource>) : Iterable<TSource> {
    override fun iterator(): Iterator<TSource> = source.iterator()

    /**
     * Applies an accumulator function over a sequence.
     * @param [func] An accumulator function to be invoked on each element.
     * @return The final accumulator value.
     * @throws [NullPointerException] [func] is null.
     * @throws [UnsupportedOperationException] source contains no elements.
     */
    fun aggregate(func: (TSource, TSource) -> TSource): TSource = source.reduce(func)

    /**
     * Applies an accumulator function over a sequence. The specified seed value is used as the initial accumulator value.
     * @param [seed] The initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @return The final accumulator value.
     * @throws [NullPointerException] [func] is null.
     */
    fun <TAccumulate> aggregate(seed: TAccumulate, func: (TAccumulate, TSource) -> TAccumulate): TAccumulate =
        source.fold(seed, func)

    /**
     * Applies an accumulator function over a sequence. The specified seed value is used as the initial accumulator value, and the specified function is used to select the result value.
     * @param [seed] The initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @param [resultSelector] A function to transform the final accumulator value into the result value.
     * @return The transformed final accumulator value.
     * @throws [NullPointerException] [func] or [resultSelector] is null.
     */
    fun <TAccumulate, TResult> aggregate(
        seed: TAccumulate,
        func: (TAccumulate, TSource) -> TAccumulate,
        resultSelector: (TAccumulate) -> TResult
    ): TResult = resultSelector(source.fold(seed, func))

    /**
     * Applies an accumulator function over a sequence, grouping results by key.
     * @param [keySelector] A function to extract the key for each element.
     * @param [seed] The initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @return An enumerable containing the aggregates corresponding to each key deriving from source.
     */
    fun <TKey, TAccumulate> aggregateBy(
        keySelector: (TSource) -> TKey,
        seed: TAccumulate,
        func: (TAccumulate, TSource) -> TAccumulate
    ): Enumerable<Map.Entry<TKey, TAccumulate>> = Enumerable(
        source.groupingBy(keySelector)
            .fold(seed, func)
            .asSequence()
    )

    /**
     * Applies an accumulator function over a sequence, grouping results by key.
     * @param [keySelector] A function to extract the key for each element.
     * @param [seedSelector] A factory for the initial accumulator value.
     * @param [func] An accumulator function to be invoked on each element.
     * @return An enumerable containing the aggregates corresponding to each key deriving from source.
     */
    fun <TKey, TAccumulate> aggregateBy(
        keySelector: (TSource) -> TKey,
        seedSelector: (TKey, TSource) -> TAccumulate,
        func: (TKey, TAccumulate, TSource) -> TAccumulate
    ): Enumerable<Map.Entry<TKey, TAccumulate>> = Enumerable(
        source.groupingBy(keySelector)
            .fold(seedSelector, func)
            .asSequence()
    )

    /**
     * Determines whether a sequence contains any elements.
     * @return true if the source sequence contains any elements; otherwise, false.
     */
    fun any(): Boolean = source.any()

    /**
     * Determines whether any element of a sequence satisfies a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return true if the source sequence is not empty and at least one of its elements passes the test in the specified predicate; otherwise, false.
     * @throws [NullPointerException] [predicate] is null.
     */
    fun any(predicate: (TSource) -> Boolean): Boolean = source.any(predicate)

    /**
     * Determines whether all elements of a sequence satisfy a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return true if every element of the source sequence passes the test in the specified predicate, or if the sequence is empty; otherwise, false.
     * @throws [NullPointerException] [predicate] is null.
     */
    fun all(predicate: (TSource) -> Boolean): Boolean = source.all(predicate)

    /**
     * Computes the average of a sequence of [Integer] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] [selector] is null.
     */
    fun averageInt(selector: (TSource) -> Int): Double = source.map(selector).average()

    /**
     * Computes the average of a sequence of [Long] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] [selector] is null.
     */
    fun averageLong(selector: (TSource) -> Long): Double = source.map(selector).average()

    /**
     * Computes the average of a sequence of [Float] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] [selector] is null.
     */
    fun averageFloat(selector: (TSource) -> Float): Float = source.map(selector).average().toFloat()

    /**
     * Computes the average of a sequence of [Double] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] [selector] is null.
     */
    fun averageDouble(selector: (TSource) -> Double): Double = source.map(selector).average()

    /**
     * Computes the average of a sequence of [BigDecimal] values that are obtained by invoking a transform function on each element of the input sequence.
     * @param [selector] A transform function to apply to each element.
     * @return the average of the sequence of values.
     * @throws [NullPointerException] [selector] is null.
     * @throws [ArithmeticException] source contains no elements.
     */
    fun averageBigDecimal(selector: (TSource) -> BigDecimal): BigDecimal = source.map(selector).average()

    /**
     * Casts the elements of an [Enumerable] to the specified type.
     * @param [clazz] the type class.
     * @return An [Enumerable] that contains each element of the source sequence cast to the specified type.
     * @throws [NullPointerException] [clazz] is null.
     * @throws [ClassCastException] An element in the sequence cannot be cast to type TResult.
     */
    fun <TResult> cast(clazz: Class<TResult>): Enumerable<TResult> = Enumerable(source.cast(clazz))

    /**
     * Splits the elements of a sequence into chunks of size at most size.
     * @param [size] The maximum size of each chunk.
     * @return An [Enumerable] that contains the elements the input sequence split into chunks of size [size].
     * @throws [IllegalArgumentException] [size] is below 1.
     */
    fun chunk(size: Int): Enumerable<List<TSource>> = Enumerable(source.chunked(size))

    /**
     * Concatenates two sequences.
     * @param [other] The sequence to concatenate to the source.
     * @return An [Enumerable] that contains the concatenated elements of the two input sequences.
     * @throws [NullPointerException] [other] is null.
     */
    fun concat(other: Iterable<TSource>): Enumerable<TSource> = Enumerable(source + other)

    /**
     * Determines whether a sequence contains a specified element by using the default equality comparer.
     * @param [value] The value to locate in the sequence.
     * @return true if the source sequence contains an element that has the specified value; otherwise, false.
     */
    fun contains(value: TSource): Boolean = source.contains(value)

    /**
     * Returns the number of elements in a sequence.
     * @return The number of elements in the input sequence.
     */
    fun count(): Int = source.count()

    /**
     * Returns a number that represents how many elements in the specified sequence satisfy a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return A number that represents how many elements in the sequence satisfy the condition in the predicate function.
     * @throws [NullPointerException] [predicate] is null.
     */
    fun count(predicate: (TSource) -> Boolean): Int = source.count(predicate)

    /**
     * Returns a [Long] that represents the total number of elements in a sequence.
     * @return The number of elements in the input sequence.
     */
    fun longCount(): Long = source.longCount()

    /**
     * Returns a [Long] that represents how many elements in a sequence satisfy a condition.
     * @param [predicate] A function to test each element for a condition.
     * @return A number that represents how many elements in the sequence satisfy the condition in the predicate function.
     * @throws [NullPointerException] [predicate] is null.
     */
    fun longCount(predicate: (TSource) -> Boolean): Long = source.longCount(predicate)

    /**
     * Returns the count of elements in the source sequence grouped by key.
     * @param [keySelector] A function to extract the key for each element.
     * @return An enumerable containing the frequencies of each key occurrence in source.
     * @throws [NullPointerException] [keySelector] is null.
     */
    fun <TKey> countBy(keySelector: (TSource) -> TKey): Enumerable<Map.Entry<TKey, Int>> =
        Enumerable(source.groupingBy(keySelector).eachCount().asSequence())

    /**
     * Returns distinct elements from a sequence by using the default equality comparer to compare values.
     * @return An Enumerable that contains distinct elements from the source sequence.
     */
    fun distinct(): Enumerable<TSource> = Enumerable(source.distinct())

    /**
     * Returns distinct elements from a sequence according to a specified key selector function.
     * @param [keySelector] A function to extract the key for each element.
     * @return An Enumerable that contains distinct elements from the source sequence.
     * @throws [NullPointerException] [keySelector] is null.
     */
    fun <TKey> distinctBy(keySelector: (TSource) -> TKey): Enumerable<TSource> =
        Enumerable(source.distinctBy(keySelector))

    /**
     * Returns the element at a specified index in a sequence.
     * @param [index] The zero-based index of the element to retrieve.
     * @return The element at the specified position in the source sequence.
     * @throws [IndexOutOfBoundsException] index is less than 0 or greater than or equal to the number of elements in source.
     */
    fun elementAt(index: Int): TSource = source.elementAt(index)

    /**
     * Returns the element at a specified index in a sequence or null if the index is out of range.
     * @param [index] The zero-based index of the element to retrieve.
     * @return null if the index is outside the bounds of the source sequence; otherwise, the element at the specified position in the source sequence.
     */
    fun elementAtOrDefault(index: Int): TSource? = source.elementAtOrNull(index)

    /**
     * Produces the set difference of two sequences by using the default equality comparer to compare values.
     * @param [other] An Iterable whose elements that also occur in the sequence will cause those elements to be removed from the returned sequence.
     * @return A sequence that contains the set difference of the elements of two sequences.
     * @throws [NullPointerException] [other] is null.
     */
    fun except(other: Iterable<TSource>): Enumerable<TSource> = Enumerable(source.except(other))

    /**
     * Produces the set difference of two sequences according to a specified key selector function.
     * @param [other] An Iterable whose keys that also occur in the sequence will cause those elements to be removed from the returned sequence.
     * @param [keySelector] A function to extract the key for each element.
     * @return A sequence that contains the set difference of the elements of two sequences.
     * @throws [NullPointerException] [other] or [keySelector] is null.
     */
    fun <TKey> exceptBy(other: Iterable<TKey>, keySelector: (TSource) -> TKey): Enumerable<TSource> =
        Enumerable(source.exceptBy(other, keySelector))

    /**
     * Returns the first element of a sequence.
     * @return The first element in the specified sequence.
     * @throws [NoSuchElementException] The source sequence is empty.
     */
    fun first(): TSource = source.first()

    /**
     * Returns the first element of a sequence.
     *
     * @param [predicate] A function to test each element for a condition.
     * @return The first element in the sequence that passes the test in the specified predicate function.
     * @throws [NullPointerException]  [predicate] is null.
     * @throws [NoSuchElementException] No element satisfies the condition in predicate. -or- The source sequence is empty.
     */
    fun first(predicate: (TSource) -> Boolean): TSource = source.first(predicate)

    /**
     * Returns the first element of a sequence, or null if the sequence contains no elements.
     * @return null if source is empty otherwise, the first element in source.
     */
    fun firstOrDefault(): TSource? = source.firstOrNull()

    /**
     * Returns the first element of a sequence, or a specified default value if the sequence contains no elements.
     * @param [defaultValue] The default value to return if the sequence is empty.
     * @return [defaultValue] if source is empty; otherwise, the first element in source.
     */
    fun firstOrDefault(defaultValue: TSource): TSource = source.firstOrNull() ?: defaultValue

    /**
     * Returns the first element of the sequence that satisfies a condition or a default value if no such element is found.
     * @param [predicate] A function to test each element for a condition.
     * @return null if source is empty or if no element passes the test specified by predicate otherwise, the first element in source that passes the test specified by predicate.
     * @throws [NullPointerException] [predicate] is null.
     */
    fun firstOrDefault(predicate: (TSource) -> Boolean): TSource? = source.firstOrNull(predicate)

    /**
     * Returns the first element of the sequence that satisfies a condition or a default value if no such element is found.
     *
     * @param [predicate] A function to test each element for a condition.
     * @param [defaultValue] The default value to return if the sequence is empty.
     * @return [defaultValue] if source is empty or if no element passes the test specified by predicate; otherwise, the first element in source that passes the test specified by predicate.
     * @throws [NullPointerException] [predicate] is null.
     */
    fun firstOrDefault(predicate: (TSource) -> Boolean, defaultValue: TSource): TSource =
        source.firstOrNull(predicate) ?: defaultValue

    fun <TInner, TKey, TResult> groupJoin(
        inner: Iterable<TInner>,
        outerKeySelector: (TSource) -> TKey,
        innerKeySelector: (TInner) -> TKey,
        resultSelector: (TSource, Enumerable<TInner>) -> TResult
    ): Enumerable<TResult> = Enumerable(source.groupJoin(inner, outerKeySelector, innerKeySelector, resultSelector))

    fun <TKey> groupBy(keySelector: (TSource) -> TKey): Enumerable<Group<TKey?, TSource>> =
        Enumerable(source.groupBy(keySelector))

    fun <TKey, TElement> groupBy(
        keySelector: (TSource) -> TKey,
        elementSelector: (TSource) -> TElement
    ): Enumerable<Group<TKey?, TElement>> = Enumerable(source.groupBy(keySelector, elementSelector))

    fun index(): Enumerable<Tuple<Int, TSource>> = Enumerable(source.index())

    fun intersect(other: Iterable<TSource>): Enumerable<TSource> = Enumerable(source.intersect(other))

    fun <TKey> intersectBy(other: Iterable<TKey>, keySelector: (TSource) -> TKey): Enumerable<TSource> =
        Enumerable(source.intersectBy(other, keySelector))

    fun <TInner, TKey, TResult> join(
        inner: Iterable<TInner>,
        outerKeySelector: (TSource) -> TKey,
        innerKeySelector: (TInner) -> TKey,
        resultSelector: (TSource, TInner) -> TResult
    ): Enumerable<TResult> = Enumerable(source.join(inner, outerKeySelector, innerKeySelector, resultSelector))

    fun last(): TSource = source.last()

    fun last(predicate: (TSource) -> Boolean): TSource = source.last(predicate)

    fun lastOrDefault(): TSource? = source.lastOrNull()

    fun lastOrDefault(predicate: (TSource) -> Boolean): TSource? = source.lastOrNull(predicate)

    fun lastOrDefault(defaultValue: TSource): TSource = source.lastOrNull() ?: defaultValue

    fun lastOrDefault(predicate: (TSource) -> Boolean, defaultValue: TSource): TSource? =
        source.lastOrNull(predicate) ?: defaultValue

    fun <TInner, TKey, TResult> leftJoin(
        inner: Iterable<TInner>,
        outerKeySelector: (TSource) -> TKey,
        innerKeySelector: (TInner) -> TKey,
        resultSelector: (TSource, TInner?) -> TResult
    ): Enumerable<TResult> = Enumerable(source.leftJoin(inner, outerKeySelector, innerKeySelector, resultSelector))

    fun max(comparer: Comparator<TSource>): TSource = source.maxWith(comparer)

    fun <TResult : Comparable<TResult>> max(selector: (TSource) -> TResult): TResult = source.maxOf(selector)

    fun <TResult> max(selector: (TSource) -> TResult, comparer: Comparator<TResult>): TResult =
        source.maxOfWith(comparer, selector)

    fun <TKey : Comparable<TKey>> maxBy(selector: (TSource) -> TKey): TSource =
        source.maxByOrNull(selector) ?: throw NoSuchElementException()

    fun <TKey> maxBy(selector: (TSource) -> TKey, comparer: Comparator<TKey>): TSource =
        source.maxBy(selector, comparer)

    fun min(comparer: Comparator<TSource>): TSource = source.minWith(comparer)

    fun <TResult : Comparable<TResult>> min(selector: (TSource) -> TResult): TResult = source.minOf(selector)

    fun <TResult> min(selector: (TSource) -> TResult, comparer: Comparator<TResult>): TResult =
        source.minOfWith(comparer, selector)

    fun <TKey : Comparable<TKey>> minBy(selector: (TSource) -> TKey): TSource =
        source.minByOrNull(selector) ?: throw NoSuchElementException()

    fun <TKey> minBy(selector: (TSource) -> TKey, comparer: Comparator<TKey>): TSource =
        source.minBy(selector, comparer)

    fun <TResult> ofType(clazz: Class<TResult>): Enumerable<TResult> = Enumerable(source.ofType(clazz))

    fun order(comparer: Comparator<TSource>): Enumerable<TSource> = Enumerable(source.sortedWith(comparer))

    fun <TKey : Comparable<TKey>> orderBy(keySelector: (TSource) -> TKey): Enumerable<TSource> =
        Enumerable(source.sortedBy(keySelector))

    fun <TKey> orderBy(keySelector: (TSource) -> TKey, comparer: Comparator<TKey>): Enumerable<TSource> =
        Enumerable(source.sortedWith { a, b -> comparer.compare(keySelector(a), keySelector(b)) })

    fun <TKey : Comparable<TKey>> orderByDescending(keySelector: (TSource) -> TKey): Enumerable<TSource> =
        Enumerable(source.sortedByDescending(keySelector))

    fun reverse(): Enumerable<TSource> = Enumerable(source.toList().asReversed().asSequence())

    fun <TInner, TKey, TResult> rightJoin(
        inner: Iterable<TInner>,
        outerKeySelector: (TSource) -> TKey,
        innerKeySelector: (TInner) -> TKey,
        resultSelector: (TSource?, TInner) -> TResult
    ): Enumerable<TResult> = Enumerable(source.rightJoin(inner, outerKeySelector, innerKeySelector, resultSelector))

    /**
     * Projects each element of a sequence into a new form.
     * @param [selector] A transform function to apply to each element.
     * @return An Enumerable<out T> whose elements are the result of invoking the transform function on each element of source.
     * @exception [NullPointerException] [selector] is null.
     */
    fun <TResult> select(selector: (TSource) -> TResult): Enumerable<TResult> = Enumerable(source.map(selector))

    /**
     * Projects each element of a sequence into a new form by incorporating the element's index.
     * @param [selector] A transform function to apply to each source element; the second parameter of the function represents the index of the source element.
     * @return An Enumerable<out T> whose elements are the result of invoking the transform function on each element of source.
     * @exception [NullPointerException] [selector] is null.
     */
    fun <TResult> select(selector: (TSource, Int) -> TResult): Enumerable<TResult> =
        Enumerable(source.mapIndexed { index, e -> selector(e, index) })

    fun <TResult> selectMany(selector: (TSource) -> Iterable<TResult>): Enumerable<TResult> =
        Enumerable(source.selectMany(selector))

    fun <TResult> selectMany(selector: (TSource, Int) -> Iterable<TResult>): Enumerable<TResult> =
        Enumerable(source.selectMany(selector))

    fun <TCollection, TResult> selectMany(
        collectionSelector: (TSource) -> Iterable<TCollection>,
        resultSelector: (TSource, TCollection) -> TResult
    ): Enumerable<TResult> = Enumerable(source.selectMany(collectionSelector, resultSelector))

    fun <TCollection, TResult> selectMany(
        collectionSelector: (TSource, Int) -> Iterable<TCollection>,
        resultSelector: (TSource, TCollection) -> TResult
    ): Enumerable<TResult> = Enumerable(source.selectMany(collectionSelector, resultSelector))

    fun shuffle(): Enumerable<TSource> = Enumerable(source.shuffled())

    fun single(): TSource = source.single()

    fun single(predicate: (TSource) -> Boolean): TSource = source.single(predicate)

    fun singleOrDefault(): TSource? = source.singleOrNull()

    fun singleOrDefault(defaultValue: TSource): TSource = this.singleOrNull() ?: defaultValue

    fun singleOrDefault(predicate: (TSource) -> Boolean): TSource? = source.singleOrNull(predicate)

    fun singleOrDefault(defaultValue: TSource, predicate: (TSource) -> Boolean): TSource =
        source.singleOrNull(predicate) ?: defaultValue

    fun skip(count: Int): Enumerable<TSource> = Enumerable(source.drop(count))

    fun skipWhile(predicate: (TSource) -> Boolean): Enumerable<TSource> = Enumerable(source.dropWhile(predicate))

    fun skipWhile(predicate: (TSource, Int) -> Boolean): Enumerable<TSource> = Enumerable(source.skipWhile(predicate))

    fun skipLast(count: Int): Enumerable<TSource> = Enumerable(source.skipLast(count))

    fun sumInt(selector: (TSource) -> Int): Int = source.sumOf(selector)

    fun sumLong(selector: (TSource) -> Long): Long = source.sumOf(selector)

    fun sumFloat(selector: (TSource) -> Float): Float = source.sum(selector)

    fun sumDouble(selector: (TSource) -> Double): Double = source.sumOf(selector)

    fun sumBigDecimal(selector: (TSource) -> BigDecimal): BigDecimal = source.sumOf(selector)

    fun take(count: Int): Enumerable<TSource> = Enumerable(source.take(count))

    fun take(startInclusive: Int, endExclusive: Int): Enumerable<TSource> {
        val range = startInclusive until endExclusive
        return Enumerable(source.drop(startInclusive).take(range.count()))
    }

    fun takeWhile(predicate: (TSource) -> Boolean): Enumerable<TSource> =
        Enumerable(source.takeWhile(predicate))

    fun takeWhile(predicate: (TSource, Int) -> Boolean): Enumerable<TSource> =
        Enumerable(source.takeWhile(predicate))

    fun takeLast(count: Int): Enumerable<TSource> = Enumerable(source.takeLast(count))

    fun union(other: Iterable<TSource>) = Enumerable(source.union(other))

    fun <TKey> unionBy(other: Iterable<TSource>, keySelector: (TSource) -> TKey) =
        Enumerable(source.unionBy(other, keySelector))

    fun where(predicate: (TSource) -> Boolean): Enumerable<TSource> = Enumerable(source.filter(predicate))

    fun where(predicate: (TSource, Int) -> Boolean): Enumerable<TSource> =
        Enumerable(source.filterIndexed { index, source -> predicate(source, index) })

    fun <TOther> zip(other: Iterable<TOther>): Enumerable<Tuple<TSource, TOther>> = Enumerable(source.zip(other))

    fun <TOther, TResult> zip(
        other: Iterable<TOther>,
        resultSelector: (TSource, TOther) -> TResult
    ): Enumerable<TResult> = Enumerable(source.zip(other, resultSelector))

    fun toList(): List<TSource> = source.toMutableList()

    fun <TKey> toLookUp(keySelector: (TSource) -> TKey): LookUp<TKey, TSource> = source.toLookUp(keySelector)

    fun <TKey, TElement> toLookUp(
        keySelector: (TSource) -> TKey?,
        elementSelector: (TSource) -> TElement
    ): LookUp<TKey, TElement> = source.toLookUp(keySelector, elementSelector)

    fun <TKey> toMap(keySelector: (TSource) -> TKey): Map<TKey, TSource> =
        source.associateByTo(mutableMapOf(), keySelector)

    fun <TKey, TElement> toMap(
        keySelector: (TSource) -> TKey,
        elementSelector: (TSource) -> TElement
    ): Map<TKey, TElement> =
        source.associateByTo(mutableMapOf(), keySelector, elementSelector)

    fun toHashSet(): Set<TSource> = source.toHashSet()
}