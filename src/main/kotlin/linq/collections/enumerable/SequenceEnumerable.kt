package linq.collections.enumerable

import linq.Enumerable
import linq.Enumerator
import linq.collections.enumerator.SequenceEnumerator

class SequenceEnumerable<T>(private val sequence: Sequence<T>) : Enumerable<T>() {
    override fun enumerator(): Enumerator<T> {
        return SequenceEnumerator(sequence)
    }

    override fun iterator(): Iterator<T> {
        return sequence.iterator()
    }
}