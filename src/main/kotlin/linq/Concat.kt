package linq

import linq.collections.enumerable.IterableEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource> concat(first: Enumerable<TSource>?, second: Iterable<TSource>?): Enumerable<TSource> {
    if (first == null) {
        throwArgumentNullException(ExceptionArgument.First)
    }

    if (second == null) {
        throwArgumentNullException(ExceptionArgument.Second)
    }

    if (isEmptyArray(first)) {
        return IterableEnumerable(second)
    }

    if (isEmptyArray(second)) {
        return first
    }

    return if (first is ConcatIterator<TSource>) {
        first.concat(IterableEnumerable(second))
    } else {
        Concat2Iterator(first, IterableEnumerable(second))
    }
}

private class Concat2Iterator<TSource>(
    private val first: Enumerable<TSource>,
    private val second: Enumerable<TSource>
) : ConcatIterator<TSource>() {
    override fun clone() = Concat2Iterator(first, second)

    override fun concat(next: Enumerable<TSource>): ConcatIterator<TSource> {
        return ConcatNIterator(this, next, 2)
    }

    override fun enumerable(index: Int): Enumerable<TSource>? {
        assert(index in 0..2)

        return when (index) {
            0 -> first
            1 -> second
            else -> null
        }
    }

    override fun itCount(): Int {
        var firstCount = 0
        var secondCount = 0
        if (first.tryGetNonEnumeratedCount() == null) {
            firstCount = first.count()
        }

        if (first.tryGetNonEnumeratedCount() == null) {
            secondCount = first.count()
        }
        return firstCount + secondCount
    }

    override fun tryGetElementAt(index: Int): TSource? {
        var index = index
        if (index >= 0) {
            for (source in sequenceOf(first, second)) {
                source.tryGetNonEnumeratedCount()?.let {
                    if (index < it) {
                        return source.elementAt(index)
                    }
                    index -= it
                } ?: run {
                    source.enumerator().use {
                        while (it.moveNext()) {
                            if (index == 0) {
                                return it.current
                            }

                            index--
                        }
                    }
                }
            }
        }
        return null
    }

    override fun tryGetFirst(): TSource? {
        return first.tryGetFirst() ?: second.tryGetFirst()
    }

    override fun tryGetLast(): TSource? {
        return first.tryGetLast() ?: second.tryGetLast()
    }

    override fun itContains(value: TSource): Boolean {
        return first.contains(value) || second.contains(value)
    }
}

private class ConcatNIterator<TSource>(
    private val tail: ConcatIterator<TSource>,
    private val head: Enumerable<TSource>,
    private val headIndex: Int,
) : ConcatIterator<TSource>() {
    init {
        assert(headIndex >= 2)
    }

    private val previousN: ConcatNIterator<TSource>?
        get() = tail as? ConcatNIterator<TSource>

    override fun clone(): AbstractIterator<TSource> = ConcatNIterator(tail, head, headIndex)

    override fun concat(next: Enumerable<TSource>): ConcatIterator<TSource> {
        if (headIndex == Int.MAX_VALUE - 2) {
            return Concat2Iterator(this, next)
        }

        return ConcatNIterator(this, next, headIndex + 1)
    }

    override fun enumerable(index: Int): Enumerable<TSource>? {
        assert(index >= 0)

        if (index > headIndex) {
            return null
        }

        var node: ConcatNIterator<TSource>?
        var previousN: ConcatNIterator<TSource>? = this
        do {
            node = previousN
            if (index == node!!.headIndex) {
                return node.head
            }
            previousN = node.previousN
        } while (previousN != null)

        assert(index == 0 || index == 1)
        assert(node.tail is Concat2Iterator<TSource>)
        return node.tail.enumerable(index)
    }

    override fun itCount(): Int {
        var count = 0
        var node: ConcatNIterator<TSource>?
        var previousN: ConcatNIterator<TSource>? = this

        do {
            node = previousN
            val source = node!!.head
            count += source.count()
            previousN = node.previousN
        } while (previousN != null)

        assert(node.tail is Concat2Iterator<TSource>)
        return count + node.tail.itCount()
    }

    override fun tryGetElementAt(index: Int): TSource? {
        var index = index
        if (index >= 0) {
            var i = 0
            var source: Enumerable<TSource>?
            while (true) {
                source = enumerable(i) ?: break
                source.tryGetNonEnumeratedCount()?.let {
                    if (index < it) {
                        return source.elementAt(index)
                    }
                    index -= it
                } ?: run {
                    source.enumerator().use {
                        while (it.moveNext()) {
                            if (index == 0) {
                                return it.current
                            }

                            index--
                        }
                    }
                }
                i++
            }
        }
        return null
    }

    override fun tryGetFirst(): TSource? {
        var i = 0
        var source: Enumerable<TSource>?
        while (true) {
            source = enumerable(i) ?: break
            source.tryGetFirst()?.let {
                return it
            }
            i++
        }

        return null
    }

    override fun tryGetLast(): TSource? {
        var node: ConcatNIterator<TSource>?
        var previousN: ConcatNIterator<TSource>? = this
        do {
            node = previousN
            node!!.head.tryGetLast()?.let {
                return it
            }
            previousN = node.previousN
        } while (previousN != null)

        assert(node.tail is Concat2Iterator<TSource>)
        return node.tail.tryGetLast()
    }

    override fun itContains(value: TSource): Boolean {
        var node: ConcatNIterator<TSource>?
        var previousN: ConcatNIterator<TSource>? = this
        do {
            node = previousN
            if (node!!.head.contains(value)) {
                return true
            }
            previousN = node.previousN
        } while (previousN != null)

        assert(node.tail is Concat2Iterator<TSource>)
        return node.tail.contains(value)
    }
}

private abstract class ConcatIterator<TSource> : AbstractIterator<TSource>() {
    private var enumerator: Enumerator<TSource>? = null

    override fun close() {
        enumerator?.close()
        enumerator = null
        super.close()
    }

    abstract fun enumerable(index: Int): Enumerable<TSource>?

    abstract fun concat(next: Enumerable<TSource>): ConcatIterator<TSource>

    override fun moveNext(): Boolean {
        if (state == 1) {
            enumerator = enumerable(0)!!.enumerator()
            state = 2
        }
        if (state > 1) {
            while (true) {
                val enumerator = enumerator.assertNotNull()
                if (enumerator.moveNext()) {
                    current = enumerator.current
                    return true
                }

                val next = enumerable(state++ - 1)
                if (next != null) {
                    enumerator.close()
                    this.enumerator = next.enumerator()
                    continue
                }

                close()
                break
            }
        }

        return false
    }

    override fun getList(): List<TSource> {
        val list = mutableListOf<TSource>()

        var i = 0
        while (true) {
            val source = enumerable(i) ?: break
            list.addAll(source)
            i++
        }

        return list
    }
}