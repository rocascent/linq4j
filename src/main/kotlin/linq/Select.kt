package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.collections.enumerable.EmptyEnumerable
import linq.collections.enumerable.SequenceEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import kotlin.math.min

fun <TSource, TResult> select(source: Enumerable<TSource>?, selector: ((TSource) -> TResult)?): Enumerable<TResult> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }
    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    if (source is AbstractIterator<TSource>) {
        return source.itSelect(selector)
    }

    if (source is ArrayEnumerable<TSource>) {
        if (source.count() == 0) return EmptyEnumerable()
        return ArraySelectIterator(source, selector)
    }

    return EnumerableSelectIterator(source, selector)
}

fun <TSource, TResult> select(
    source: Enumerable<TSource>?,
    selector: ((TSource, Int) -> TResult)?
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

    return SequenceEnumerable(selectIterator(source, selector))
}

private fun <TSource, TResult> selectIterator(
    source: Enumerable<TSource>,
    selector: (TSource, Int) -> TResult
): Sequence<TResult> = sequence {
    var index = -1
    for (element in source) {
        index++
        yield(selector(element, index))
    }
}

internal class ArraySelectIterator<TSource, TResult>(
    private val source: ArrayEnumerable<TSource>,
    private val selector: (TSource) -> TResult
) : AbstractIterator<TResult>() {
    init {
        assert(source.size > 0)
    }

    override fun clone(): AbstractIterator<TResult> = ArraySelectIterator(source, selector)

    override fun moveNext(): Boolean {
        val index = state - 1
        if (index < source.size) {
            state++
            currentField = selector(source[index])
            return true
        }

        close()
        return false
    }

    override fun <TResult2> itSelect(selector: (TResult) -> TResult2): Enumerable<TResult2> =
        ArraySelectIterator(source, this.selector.then(selector))

    override fun getList(): List<TResult> {
        val count = source.size
        return List(count) { selector(source[it]) }
    }

    override fun itCount(): Int {
        for (item in source) {
            selector(item)
        }
        return source.size
    }

    override fun itSkip(count: Int): AbstractIterator<TResult>? {
        assert(count > 0)
        if (count >= source.size) {
            return null
        }
        return ArraySkipTakeSelectIterator(source, selector, count, Int.MAX_VALUE)
    }

    override fun itTake(count: Int): AbstractIterator<TResult>? {
        assert(count > 0)
        return if (count >= source.size) {
            this
        } else {
            ArraySkipTakeSelectIterator(source, selector, 0, count - 1)
        }
    }

    override fun tryGetElementAt(index: Int): TResult? {
        if (index.toUInt() < source.size.toUInt()) {
            return selector(source[index])
        }
        return null;
    }

    override fun tryGetFirst(): TResult {
        assert(source.size > 0)
        return selector(source[0])
    }

    override fun tryGetLast(): TResult {
        assert(source.size > 0)
        return selector(source[source.size - 1])
    }
}

internal class EnumerableSelectIterator<TSource, TResult>(
    private val source: Enumerable<TSource>,
    private val selector: (TSource) -> TResult
) : AbstractIterator<TResult>() {
    private var enumerator: Enumerator<TSource>? = null

    override fun clone(): AbstractIterator<TResult> {
        return EnumerableSelectIterator(source, selector)
    }

    override fun close() {
        enumerator?.close()
        enumerator = null
        super.close()
    }

    override fun moveNext(): Boolean {
        loop@ while (true) {
            when (state) {
                1 -> {
                    enumerator = source.enumerator()
                    state = 2
                    continue@loop
                }

                2 -> {
                    assert(enumerator != null)
                    if (enumerator!!.moveNext()) {
                        currentField = selector(enumerator!!.current)
                        return true
                    }

                    close()
                    break@loop
                }
            }
        }
        return false
    }

    override fun <TResult2> itSelect(selector: (TResult) -> TResult2): Enumerable<TResult2> {
        return EnumerableSelectIterator(source, this.selector.then(selector))
    }

    override fun getList(): List<TResult> {
        return source.mapTo(arrayListOf(), selector)
    }

    override fun itCount(): Int {
        var count = 0

        for (item in source) {
            selector(item)
            count++
        }

        return count
    }

    override fun tryGetFirst(): TResult? {
        source.enumerator().use {
            if (it.moveNext()) {
                return selector(it.current)
            }
            return null
        }
    }
}

class ArraySkipTakeSelectIterator<TSource, TResult>(
    private val source: ArrayEnumerable<TSource>,
    private val selector: (TSource) -> TResult,
    private val minIndexInclusive: Int,
    private val maxIndexInclusive: Int
) : AbstractIterator<TResult>() {
    init {
        assert(minIndexInclusive >= 0)
        assert(minIndexInclusive <= maxIndexInclusive)
    }

    override fun clone(): AbstractIterator<TResult> {
        return ArraySkipTakeSelectIterator(source, selector, minIndexInclusive, maxIndexInclusive)
    }

    override fun moveNext(): Boolean {
        val index = state - 1
        if (index <= maxIndexInclusive - minIndexInclusive && index < source.size - minIndexInclusive) {
            currentField = selector(source[minIndexInclusive + index])
            ++state
            return true
        }

        close()
        return false
    }

    override fun <TResult2> itSelect(selector: (TResult) -> TResult2): Enumerable<TResult2> {
        return ArraySkipTakeSelectIterator(
            source,
            this.selector.then(selector),
            minIndexInclusive,
            maxIndexInclusive
        )
    }

    override fun itSkip(count: Int): AbstractIterator<TResult>? {
        assert(count > 0)
        val minIndex = minIndexInclusive + count
        return if (minIndex > maxIndexInclusive) {
            null
        } else {
            ArraySkipTakeSelectIterator(source, selector, minIndex, maxIndexInclusive)
        }
    }

    override fun tryGetFirst(): TResult? {
        if (source.size > minIndexInclusive) {
            return selector(source[minIndexInclusive])
        }
        return null
    }

    val count: Int
        get() {
            val count = source.size
            if (count <= minIndexInclusive) {
                return 0
            }
            return min(count - 1, maxIndexInclusive) - minIndexInclusive + 1
        }

    override fun getList(): List<TResult> {
        val count = count
        if (count == 0) {
            return arrayListOf()
        }

        var sourceIndex = minIndexInclusive
        val list = arrayListOf<TResult>()
        for (i in 0 until count) {
            list.add(selector(source[sourceIndex]))
            sourceIndex++
        }

        return list
    }

    override fun itCount(): Int {
        val count = count

        val end = minIndexInclusive + count
        for (i in minIndexInclusive until end) {
            selector(source[i])
        }

        return count
    }
}