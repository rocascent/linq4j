package linq.collections.enumerable

import linq.Enumerable
import linq.Enumerator
import linq.collections.enumerator.IterableEnumerator

class IterableEnumerable<T>(private val iterable: Iterable<T>) : Enumerable<T>() {
    override fun enumerator(): Enumerator<T> {
        return IterableEnumerator(iterable)
    }

    override fun iterator(): Iterator<T> {
        return iterable.iterator()
    }
}