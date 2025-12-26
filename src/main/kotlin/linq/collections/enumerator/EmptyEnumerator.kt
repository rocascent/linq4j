package linq.collections.enumerator

import linq.Enumerator

class EmptyEnumerator<T> : Enumerator<T> {
    override fun moveNext(): Boolean = false

    override val current: T get() = throw IllegalStateException()
}