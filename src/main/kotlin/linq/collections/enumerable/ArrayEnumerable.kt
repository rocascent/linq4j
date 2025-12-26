package linq.collections.enumerable

import linq.Enumerable
import linq.Enumerator
import linq.collections.enumerator.ArrayEnumerator

class ArrayEnumerable<T>(private val source: Array<out T>) : Enumerable<T>() {
    val size: Int = source.size

    operator fun get(index: Int): T = source[index]

    override fun enumerator(): Enumerator<T> = ArrayEnumerator(source)
}