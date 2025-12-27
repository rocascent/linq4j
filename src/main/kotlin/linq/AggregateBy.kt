package linq

import linq.collections.enumerable.EmptyEnumerable
import linq.collections.enumerable.SequenceEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource, TKey, TAccumulate> aggregateBy(
    source: Enumerable<TSource>?,
    keySelector: ((TSource) -> TKey)?,
    seed: TAccumulate,
    func: ((TAccumulate, TSource) -> TAccumulate)?,
    keyComparer: EqualityComparer<TKey>? = null
): Enumerable<Map.Entry<TKey, TAccumulate>> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }
    if (keySelector == null) {
        throwArgumentNullException(ExceptionArgument.KeySelector)
    }
    if (func == null) {
        throwArgumentNullException(ExceptionArgument.Func)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(aggregateByIterator(source, keySelector, seed, func, keyComparer))
}

fun <TSource, TKey, TAccumulate> aggregateBy(
    source: Enumerable<TSource>?,
    keySelector: ((TSource) -> TKey)?,
    seedSelector: ((TKey) -> TAccumulate)?,
    func: ((TAccumulate, TSource) -> TAccumulate)?,
    keyComparer: EqualityComparer<TKey>? = null
): Enumerable<Map.Entry<TKey, TAccumulate>> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }
    if (keySelector == null) {
        throwArgumentNullException(ExceptionArgument.KeySelector)
    }
    if (seedSelector == null) {
        throwArgumentNullException(ExceptionArgument.SeedSelector)
    }
    if (func == null) {
        throwArgumentNullException(ExceptionArgument.Func)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(aggregateByIterator(source, keySelector, seedSelector, func, keyComparer))
}

private fun <TSource, TKey, TAccumulate> aggregateByIterator(
    source: Enumerable<TSource>,
    keySelector: (TSource) -> TKey,
    seed: TAccumulate,
    func: (TAccumulate, TSource) -> TAccumulate,
    keyComparer: EqualityComparer<TKey>? = null
): Sequence<Map.Entry<TKey, TAccumulate>> = sequence {
    source.enumerator().use {
        if (!it.moveNext()) {
            return@sequence
        }

        fun populateDictionary(
            enumerator: Enumerator<TSource>,
            keySelector: (TSource) -> TKey,
            seed: TAccumulate,
            func: (TAccumulate, TSource) -> TAccumulate,
            keyComparer: EqualityComparer<TKey>? = null
        ): Map<TKey, TAccumulate> {
            val comparator = if (keyComparer == null) null
            else Comparator<TKey> { a, b -> if (keyComparer(a, b)) 0 else 1 }

            val dict = sortedMapOf<TKey, TAccumulate>(comparator as Comparator<TKey>)

            do {
                val value = enumerator.current
                val key = keySelector(value)
                dict[key] = func(dict[key] ?: seed, value)
            } while (enumerator.moveNext())

            return dict
        }

        for (countBy in populateDictionary(
            it,
            keySelector,
            seed,
            func,
            keyComparer
        )) {
            yield(countBy)
        }
    }
}

private fun <TSource, TKey, TAccumulate> aggregateByIterator(
    source: Enumerable<TSource>,
    keySelector: (TSource) -> TKey,
    seedSelector: (TKey) -> TAccumulate,
    func: (TAccumulate, TSource) -> TAccumulate,
    keyComparer: EqualityComparer<TKey>?
): Sequence<Map.Entry<TKey, TAccumulate>> = sequence {
    source.enumerator().use {
        if (!it.moveNext()) {
            return@sequence
        }

        fun populateDictionary(
            enumerator: Enumerator<TSource>,
            keySelector: (TSource) -> TKey,
            seedSelector: (TKey) -> TAccumulate,
            func: (TAccumulate, TSource) -> TAccumulate,
            keyComparer: EqualityComparer<TKey>?
        ): Map<TKey, TAccumulate> {
            val comparator = if (keyComparer == null) null
            else Comparator<TKey> { a, b -> if (keyComparer(a, b)) 0 else 1 }

            val dict = sortedMapOf<TKey, TAccumulate>(comparator as Comparator<TKey>)

            do {
                val value = enumerator.current
                val key = keySelector(value)
                dict[key] = func(dict[key] ?: seedSelector(key), value)
            } while (enumerator.moveNext())

            return dict
        }

        for (countBy in populateDictionary(
            it,
            keySelector,
            seedSelector,
            func,
            keyComparer
        )) {
            yield(countBy)
        }
    }
}