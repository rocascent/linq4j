package linq

import linq.collections.enumerable.EmptyEnumerable
import linq.collections.enumerable.SequenceEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource, TResult> selectMany(
    source: Enumerable<TSource>?,
    selector: ((TSource) -> Iterable<TResult>)?
): Enumerable<TResult> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }
    return SelectManySingleSelectorIterator(source, selector)
}

fun <TSource, TResult> selectMany(
    source: Enumerable<TSource>?,
    selector: ((TSource, Int) -> Iterable<TResult>)?
): Enumerable<TResult> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(selectManyIterator(source, selector))
}

private fun <TSource, TResult> selectManyIterator(
    source: Enumerable<TSource>,
    selector: (TSource, Int) -> Iterable<TResult>
): Sequence<TResult> = sequence {
    var index = -1
    for (element in source) {
        index++
        for (subElement in selector(element, index)) {
            yield(subElement)
        }
    }
}

fun <TSource, TCollection, TResult> selectMany(
    source: Enumerable<TSource>?,
    collectionSelector: ((TSource, Int) -> Iterable<TCollection>)?,
    resultSelector: ((TSource, TCollection) -> TResult)?
): Enumerable<TResult> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (collectionSelector == null) {
        throwArgumentNullException(ExceptionArgument.collectionSelector)
    }

    if (resultSelector == null) {
        throwArgumentNullException(ExceptionArgument.resultSelector)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(selectManyIterator(source, collectionSelector, resultSelector))
}

private fun <TSource, TCollection, TResult> selectManyIterator(
    source: Enumerable<TSource>,
    collectionSelector: (TSource, Int) -> Iterable<TCollection>,
    resultSelector: (TSource, TCollection) -> TResult
): Sequence<TResult> = sequence {
    var index = -1
    for (element in source) {
        index++
        for (subElement in collectionSelector(element, index)) {
            yield(resultSelector(element, subElement))
        }
    }
}

fun <TSource, TCollection, TResult> selectMany(
    source: Enumerable<TSource>?,
    collectionSelector: ((TSource) -> Iterable<TCollection>)?,
    resultSelector: ((TSource, TCollection) -> TResult)?
): Enumerable<TResult> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (collectionSelector == null) {
        throwArgumentNullException(ExceptionArgument.collectionSelector)
    }

    if (resultSelector == null) {
        throwArgumentNullException(ExceptionArgument.resultSelector)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(selectManyIterator(source, collectionSelector, resultSelector))
}

private fun <TSource, TCollection, TResult> selectManyIterator(
    source: Enumerable<TSource>,
    collectionSelector: (TSource) -> Iterable<TCollection>,
    resultSelector: (TSource, TCollection) -> TResult
): Sequence<TResult> = sequence {
    for (element in source) {
        for (subElement in collectionSelector(element)) {
            yield(resultSelector(element, subElement))
        }
    }
}

class SelectManySingleSelectorIterator<TSource, TResult>(
    private val source: Enumerable<TSource>,
    private val selector: (TSource) -> Iterable<TResult>
) : AbstractIterator<TResult>() {
    private var sourceEnumerator: Enumerator<TSource>? = null
    private var subEnumerator: Enumerator<TResult>? = null

    override fun clone(): AbstractIterator<TResult> {
        return SelectManySingleSelectorIterator(source, selector)
    }

    override fun close() {
        subEnumerator?.close()
        subEnumerator = null
        sourceEnumerator?.close()
        sourceEnumerator = null
        super.close()
    }

    override fun moveNext(): Boolean {
        loop@ while (true) {
            when (state) {
                1 -> {
                    sourceEnumerator = source.enumerator()
                    state = 2
                    continue@loop
                }

                2 -> {
                    assert(sourceEnumerator != null)
                    if (!sourceEnumerator!!.moveNext()) {
                        break@loop
                    }

                    val element = sourceEnumerator!!.current

                    subEnumerator = Enumerable.of(selector(element)).enumerator()
                    state = 3
                    continue@loop
                }

                3 -> {
                    assert(subEnumerator != null)
                    if (!subEnumerator!!.moveNext()) {
                        subEnumerator!!.close()
                        subEnumerator = null
                        state = 2
                        continue@loop
                    }

                    currentField = subEnumerator!!.current
                    return true
                }
            }
        }
        close()
        return false
    }

    override fun getList(): List<TResult> {
        return source.flatMapTo(arrayListOf(), selector)
    }

    override fun itCount(): Int {
        var count = 0

        for (element in source) {
            count += count(of(selector(element)))
        }

        return count
    }
}