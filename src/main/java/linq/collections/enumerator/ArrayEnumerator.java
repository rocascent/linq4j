package linq.collections.enumerator;

import linq.Enumerator;

public class ArrayEnumerator<T> implements Enumerator<T> {
    private final T[] array;
    private int index;

    public ArrayEnumerator(T[] array) {
        this.array = array;
        index = -1;
    }

    @Override
    public boolean moveNext() {
        var index = this.index + 1;
        var length = array.length;
        if (index >= length) {
            this.index = length;
            return false;
        }
        this.index = index;
        return true;
    }

    @Override
    public T current() {
        return array[index];
    }
}
