package linq.collections.enumerable;

import linq.Enumerable;
import linq.Enumerator;
import linq.collections.enumerator.ArrayEnumerator;

public class ArrayEnumerable<T> extends Enumerable<T> {
    private final T[] source;

    public ArrayEnumerable(T[] source) {
        this.source = source;
    }

    public T get(int index) {
        return source[index];
    }

    @Override
    public Enumerator<T> enumerator() {
        return new ArrayEnumerator<>(source);
    }

    @Override
    public int count() {
        return source.length;
    }
}
