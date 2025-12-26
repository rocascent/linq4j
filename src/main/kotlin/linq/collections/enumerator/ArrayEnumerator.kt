package linq.collections.enumerator;

import linq.Enumerator;

class ArrayEnumerator<T>(private val array: Array<out T>) : Enumerator<T> {
    var index: Int = -1;

    override val current: T get() = array[index]

    override fun moveNext(): Boolean {
        val index = this.index + 1;
        val length = array.size;
        if (index >= length) {
            this.index = length;
            return false;
        }
        this.index = index;
        return true;
    }
}
