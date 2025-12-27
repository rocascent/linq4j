package linq

import linq.collections.enumerable.ArrayEnumerable
import kotlin.math.max
import kotlin.math.min


class ArraySkipTakeIterator<TSource>(
    private val source: ArrayEnumerable<TSource>,
    private val minIndexInclusive: Int,
    private val maxIndexInclusive: Int
) : AbstractIterator<TSource>() {
    init {
        assert(minIndexInclusive >= 0)
        assert(minIndexInclusive <= maxIndexInclusive)
    }

    override fun clone(): AbstractIterator<TSource> =
        ArraySkipTakeIterator(source, minIndexInclusive, maxIndexInclusive)

    override fun moveNext(): Boolean {
        val index = state - 1
        if (index <= maxIndexInclusive - minIndexInclusive && index < source.size - minIndexInclusive) {
            current = source[minIndexInclusive + index]
            ++state
            return true
        }

        close()
        return false
    }

    override fun <TResult> itSelect(selector: (TSource) -> TResult): Enumerable<TResult> =
        ArraySkipTakeSelectIterator(source, selector, minIndexInclusive, maxIndexInclusive)

    override fun itSkip(count: Int): AbstractIterator<TSource>? {
        val minIndex = minIndexInclusive + count
        return if (minIndex > maxIndexInclusive) {
            null
        } else {
            ArraySkipTakeIterator(source, minIndex, maxIndexInclusive)
        }
    }

    override fun tryGetFirst(): TSource? {
        if (source.size > minIndexInclusive) {
            return source[minIndexInclusive]
        }
        return null
    }

    private fun getAdjustedCount(minIndexInclusive: Int, maxIndexInclusive: Int, sourceCount: Int): Int {
        if (sourceCount <= minIndexInclusive) {
            return 0
        }

        return min(sourceCount - 1, maxIndexInclusive) - minIndexInclusive + 1
    }

    override fun itCount(): Int = getAdjustedCount(minIndexInclusive, maxIndexInclusive, source.size)

    override fun getList(): List<TSource> {
        val count = itCount()

        val list = arrayListOf<TSource>()
        if (count != 0) {
            var sourceIndex = minIndexInclusive
            repeat(count) {
                list.add(source[sourceIndex])
                sourceIndex++
            }
        }

        return list
    }
}

class EnumerableSkipTakeIterator<TSource>(
    private val source: Enumerable<TSource>,
    private val minIndexInclusive: Int,
    private val maxIndexInclusive: Int
) : AbstractIterator<TSource>() {
    private var enumerator: Enumerator<TSource>? = null

    init {
        assert(minIndexInclusive >= 0)
        assert(maxIndexInclusive >= -1)
        assert(maxIndexInclusive == -1 || (maxIndexInclusive - minIndexInclusive < Integer.MAX_VALUE))
        { "will overflow!" }
        assert(maxIndexInclusive == -1 || minIndexInclusive <= maxIndexInclusive)
    }

    private val hasLimit: Boolean get() = maxIndexInclusive != -1

    private val limit: Int get() = maxIndexInclusive + 1 - minIndexInclusive

    override fun clone(): AbstractIterator<TSource> =
        EnumerableSkipTakeIterator(source, minIndexInclusive, maxIndexInclusive)

    override fun close() {
        enumerator?.close()
        enumerator = null
        super.close()
    }

    override fun itCount(): Int {
        if (!hasLimit) {
            return max(source.count() - minIndexInclusive, 0)
        }

        source.enumerator().use {
            val count = skipAndCount(maxIndexInclusive.toUInt() + 1u, it)
            assert(count != Int.MAX_VALUE.toUInt() + 1u || minIndexInclusive > 0) { "Our return value will be incorrect." }
            return max(count.toInt() - minIndexInclusive, 0)
        }
    }

    override fun moveNext(): Boolean {
        val taken = state - 3
        if (taken < -2) {
            close()
            return false
        }

        loop@ while (true) {
            when (state) {
                1 -> {
                    enumerator = source.enumerator()
                    state = 2
                    continue@loop
                }

                2 -> {
                    val enumerator = enumerator
                    enumerator.assertNotNull()

                    if (!skipBeforeFirst(enumerator)) {
                        break@loop
                    }

                    state = 3
                    continue@loop
                }

                else -> {
                    val enumerator = enumerator
                    enumerator.assertNotNull()
                    if ((!hasLimit || taken < limit) && enumerator.moveNext()) {
                        if (hasLimit) {
                            state++
                        }
                        current = enumerator.current
                        return true
                    }

                    break
                }
            }
        }

        close()
        return false
    }

    override fun itSkip(count: Int): AbstractIterator<TSource>? {
        val minIndex = minIndexInclusive + count

        if (!hasLimit) {
            if (minIndex < 0) {
                return EnumerableSkipTakeIterator(this, count, -1)
            }
        } else if (minIndex.toUInt() > maxIndexInclusive.toUInt()) {
            return null
        }

        assert(minIndex >= 0) { "We should have taken care of all cases when minIndex overflows." }
        return EnumerableSkipTakeIterator(source, minIndex, maxIndexInclusive)
    }

    override fun tryGetFirst(): TSource? {
        assert(!hasLimit || limit > 0)

        if (source is AbstractIterator<TSource>) {
            return source.tryGetElementAt(minIndexInclusive)
        }

        source.enumerator().use {
            if (skipBeforeFirst(it) && it.moveNext()) {
                return it.current
            }
        }

        return null
    }

    override fun getList(): List<TSource> {
        source.enumerator().use {
            if (skipBeforeFirst(it) && it.moveNext()) {
                var remaining = limit - 1
                val comparand = if (hasLimit) 0 else Int.MIN_VALUE

                val result = arrayListOf<TSource>()
                do {
                    remaining--
                    result.add(it.current)
                } while (remaining >= comparand && it.moveNext())

                return result
            }
        }

        return arrayListOf()
    }

    private fun skipBeforeFirst(en: Enumerator<TSource>): Boolean {
        return skipBefore(minIndexInclusive, en)
    }

    private fun skipBefore(index: Int, en: Enumerator<TSource>): Boolean {
        return skipAndCount(index, en) == index
    }

    private fun <TSource> skipAndCount(index: Int, en: Enumerator<TSource>): Int {
        assert(index >= 0)
        return skipAndCount(index.toUInt(), en).toInt()
    }

    private fun <TSource> skipAndCount(index: UInt, en: Enumerator<TSource>): UInt {
        for (i in 0u until index) {
            if (!en.moveNext()) {
                return i
            }
        }

        return index
    }
}