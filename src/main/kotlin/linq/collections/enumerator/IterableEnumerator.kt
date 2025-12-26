package linq.collections.enumerator;

import linq.Enumerator;

class IterableEnumerator<T>(iterable: Iterable<T>) : Enumerator<T> {
    private val iterator: Iterator<T> = iterable.iterator();
    private var _current: T? = null

    override val current: T
        get() = _current ?: throw NoSuchElementException()


    @Override
    override fun moveNext(): Boolean {
        if (iterator.hasNext()) {
            _current = iterator.next();
            return true;
        }
        return false;
    }
}
