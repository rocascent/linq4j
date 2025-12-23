package linq.collections;

import linq.Enumerator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorAdapter<TSource> implements Iterator<TSource> {
    private final Enumerator<TSource> enumerator;
    private boolean hasNext;

    public IteratorAdapter(Enumerator<TSource> enumerator) {
        this.enumerator = enumerator;
        hasNext = enumerator.moveNext();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public TSource next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        var current = enumerator.current();
        hasNext = enumerator.moveNext();
        return current;
    }
}
