package linq

import linq.collections.enumerable.ArrayEnumerable
import linq.collections.enumerable.SequenceEnumerable
import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource, TResult> cast(source: Enumerable<TSource>?, clazz: Class<TResult>): Enumerable<TResult> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (source is ArrayEnumerable<TSource>) {
        return CastArrayIterator(source, clazz)
    }

    return SequenceEnumerable(castIterator(source, clazz))
}

private fun <TSource, TResult> castIterator(source: Enumerable<TSource>, clazz: Class<TResult>): Sequence<TResult> =
    sequence {
        for (obj in source) {
            yield(clazz.cast(obj))
        }
    }

private class CastArrayIterator<TSource, TResult>(
    private val source: ArrayEnumerable<TSource>,
    private val clazz: Class<TResult>
) : AbstractIterator<TResult>() {
    private var enumerator: Enumerator<TSource>? = null

    override fun clone(): AbstractIterator<TResult> = CastArrayIterator(source, clazz)

    override fun moveNext(): Boolean {
        loop@ while (true) {
            when (state) {
                1 -> {
                    enumerator = source.enumerator()
                    state = 2
                    continue@loop
                }

                2 -> {
                    val enumerator = enumerator.assertNotNull()
                    if (enumerator.moveNext()) {
                        current = clazz.cast(enumerator.current)
                        return true
                    }

                    close()
                    break@loop
                }
            }
        }

        return false
    }

    override fun close() {
        enumerator?.close()
        enumerator = null

        super.close()
    }

    override fun getList(): List<TResult> = source.mapTo(arrayListOf()) { clazz.cast(it) }

    override fun itCount(): Int = source.size

    override fun tryGetElementAt(index: Int): TResult? {
        var index = index
        if (index >= 0) {
            source.enumerator().use {
                while (it.moveNext()) {
                    if (index == 0) {
                        return clazz.cast(it.current)
                    }

                    index--
                }
            }
        }

        return null
    }

    override fun tryGetFirst(): TResult? {
        source.enumerator().use {
            if (it.moveNext()) {
                return clazz.cast(it.current)
            }
        }

        return null
    }

    override fun tryGetLast(): TResult? = source.enumerator().use {
        if (it.moveNext()) {
            var last = clazz.cast(it.current)
            while (it.moveNext()) {
                last = clazz.cast(it.current)
            }

            return last
        }

        return null
    }

    override fun itContains(value: TResult): Boolean {
        for (item in source) {
            if (clazz.cast(item) == value) {
                return true
            }
        }

        return false
    }
}