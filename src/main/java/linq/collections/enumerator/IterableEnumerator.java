package linq.collections.enumerator;

import linq.Enumerator;

import java.util.Iterator;

public class IterableEnumerator<T> implements Enumerator<T> {
    private final Iterator<T> iterator;
    private T current = null;

    public IterableEnumerator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean moveNext() {
        if (iterator.hasNext()) {
            current = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public T current() {
        return current;
    }
}
