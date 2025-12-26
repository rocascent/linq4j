package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.collections.enumerable.EmptyEnumerable
import linq.collections.enumerable.SequenceEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource> where(source: Enumerable<TSource>?, predicate: ((TSource) -> Boolean)?): Enumerable<TSource> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.predicate)
    }

    if (source is AbstractIterator<TSource>) {
        return source.itWhere(predicate)
    }

    if (source is ArrayEnumerable<TSource>) {
        if (source.count() == 0) {
            return EmptyEnumerable()
        }

        return ArrayWhereIterator(source, predicate)
    }

    return EnumerableWhereIterator(source, predicate)
}

fun <TSource> where(source: Enumerable<TSource>?, predicate: ((TSource, Int) -> Boolean)?): Enumerable<TSource> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (predicate == null) {
        throwArgumentNullException(ExceptionArgument.predicate)
    }

    if (isEmptyArray(source)) {
        return EmptyEnumerable()
    }

    return SequenceEnumerable(whereIterator(source, predicate))
}

private fun <TSource> whereIterator(
    source: Enumerable<TSource>,
    predicate: (TSource, Int) -> Boolean
): Sequence<TSource> = sequence {
    var index = -1
    for (element in source) {
        index++
        if (predicate(element, index)) {
            yield(element)
        }
    }
}

class ArrayWhereIterator<TSource>(
    private val source: ArrayEnumerable<TSource>,
    private val predicate: (TSource) -> Boolean
) : AbstractIterator<TSource>() {
    init {
        assert(source.size > 0)
    }

    override fun clone(): AbstractIterator<TSource> {
        return ArrayWhereIterator(source, predicate)
    }

    override fun moveNext(): Boolean {
        var index = state - 1
        while (index < source.size) {
            val item = source[index]
            index = state++
            if (predicate(item)) {
                currentField = item
                return true
            }
        }

        close()
        return false
    }

    override fun <TResult> itSelect(selector: (TSource) -> TResult): Enumerable<TResult> {
        return ArrayWhereSelectIterator(source, predicate, selector)
    }

    override fun itWhere(predicate: (TSource) -> Boolean): Enumerable<TSource> {
        return ArrayWhereIterator(source, this.predicate.and(predicate))
    }

    override fun getList(): List<TSource> {
        val results = arrayListOf<TSource>()
        for (item in source) {
            if (predicate(item)) {
                results.add(item)
            }
        }
        return results
    }

    override fun itCount(): Int {
        var count = 0

        for (item in source) {
            if (predicate(item)) {
                count++
            }
        }

        return count
    }
}

class EnumerableWhereIterator<TSource>(
    private val source: Enumerable<TSource>,
    private val predicate: (TSource) -> Boolean
) : AbstractIterator<TSource>() {
    private var enumerator: Enumerator<TSource>? = null

    override fun clone(): AbstractIterator<TSource> {
        return EnumerableWhereIterator(source, predicate)
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
                    while (enumerator!!.moveNext()) {
                        val item = enumerator!!.current
                        if (predicate(item)) {
                            currentField = item
                            return true
                        }
                    }

                    close()
                    break@loop
                }
            }
        }

        return false
    }

    override fun <TResult> itSelect(selector: (TSource) -> TResult): Enumerable<TResult> {
        return EnumerableWhereSelectIterator(source, predicate, selector)
    }

    override fun itWhere(predicate: (TSource) -> Boolean): Enumerable<TSource> {
        return EnumerableWhereIterator(source, this.predicate.and(predicate))
    }

    override fun getList(): List<TSource> {
        val results = arrayListOf<TSource>()

        for (item in source) {
            if (predicate(item)) {
                results.add(item)
            }
        }

        return results
    }

    override fun itCount(): Int {
        var count = 0

        for (item in source) {
            if (predicate(item)) {
                count++
            }
        }

        return count
    }
}

class ArrayWhereSelectIterator<TSource, TResult>(
    private val source: ArrayEnumerable<TSource>,
    private val predicate: (TSource) -> Boolean,
    private val selector: (TSource) -> TResult
) : AbstractIterator<TResult>() {

    init {
        assert(source.count() > 0)
    }

    override fun clone(): AbstractIterator<TResult> {
        return ArrayWhereSelectIterator(source, predicate, selector)
    }

    override fun moveNext(): Boolean {
        var index = state - 1

        while (index < source.count()) {
            val item = source[index]
            index = state++
            if (predicate(item)) {
                currentField = selector(item)
                return true
            }
        }

        close()
        return false
    }

    override fun <TResult2> itSelect(selector: (TResult) -> TResult2): Enumerable<TResult2> {
        return ArrayWhereSelectIterator(source, predicate, this.selector.then(selector))
    }

    override fun getList(): List<TResult> {
        val results = arrayListOf<TResult>()
        for (item in source) {
            if (predicate(item)) {
                results.add(selector(item))
            }
        }
        return results
    }

    override fun itCount(): Int {
        var count = 0

        for (item in source) {
            if (predicate(item)) {
                selector(item)
                count++
            }
        }

        return count
    }
}

class EnumerableWhereSelectIterator<TSource, TResult>(
    private val source: Enumerable<TSource>,
    private val predicate: (TSource) -> Boolean,
    private val selector: (TSource) -> TResult
) : AbstractIterator<TResult>() {
    private var enumerator: Enumerator<TSource>? = null

    override fun clone(): AbstractIterator<TResult> {
        return EnumerableWhereSelectIterator(source, predicate, selector)
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
                    while (enumerator!!.moveNext()) {
                        val item = enumerator!!.current
                        if (predicate(item)) {
                            currentField = selector(item)
                            return true
                        }
                    }

                    close()
                    break@loop
                }
            }
        }
        return false
    }

    override fun <TResult2> itSelect(selector: (TResult) -> TResult2): Enumerable<TResult2> {
        return EnumerableWhereSelectIterator(source, predicate, this.selector.then(selector))
    }

    override fun getList(): List<TResult> {
        val results = arrayListOf<TResult>()

        for (item in source) {
            if (predicate(item)) {
                results.add(selector(item))
            }
        }

        return results
    }

    override fun itCount(): Int {
        var count = 0

        for (item in source) {
            if (predicate(item)) {
                selector(item)
                count++
            }
        }

        return count
    }
}