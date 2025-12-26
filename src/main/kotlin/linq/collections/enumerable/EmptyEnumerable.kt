package linq.collections.enumerable

import linq.Enumerable
import linq.Enumerator
import linq.collections.enumerator.EmptyEnumerator

class EmptyEnumerable<T> : Enumerable<T>() {
    override fun enumerator(): Enumerator<T> = EmptyEnumerator()
}