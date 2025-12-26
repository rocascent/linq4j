package linq.collections

import linq.Enumerator
import java.util.NoSuchElementException

class IteratorAdapter<TSource>(
    private val enumerator: Enumerator<TSource>
) : Iterator<TSource> {
    private var hasNext: Boolean = enumerator.moveNext()

    override fun hasNext(): Boolean = hasNext

    override fun next(): TSource {
        if (!hasNext) throw NoSuchElementException()
        val current = enumerator.current
        hasNext = enumerator.moveNext()
        return current
    }
}