package linq.collections.enumerator

import linq.Enumerator

class SequenceEnumerator<T>(private val sequence: Sequence<T>) : Enumerator<T> {
    private val iterator: Iterator<T> = sequence.iterator();
    private var _current: T? = null

    override val current: T
        get() = _current ?: throw IllegalStateException()


    @Override
    override fun moveNext(): Boolean {
        if (iterator.hasNext()) {
            _current = iterator.next();
            return true;
        }
        return false;
    }
}