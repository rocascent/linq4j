package linq.collections.enumerator

import linq.Enumerator

class SequenceEnumerator<T>(sequence: Sequence<T>) : Enumerator<T> {
    private val iterator: Iterator<T> = sequence.iterator()
    private var backingCurrent: T? = null

    override val current: T
        get() = backingCurrent ?: throw IllegalStateException()


    @Override
    override fun moveNext(): Boolean {
        if (iterator.hasNext()) {
            backingCurrent = iterator.next()
            return true
        }
        return false
    }
}