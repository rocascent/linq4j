package linq.collections.enumerable;

import linq.Enumerable;
import linq.Enumerator;
import linq.collections.enumerator.IterableEnumerator;

import java.util.Iterator;

public class IterableEnumerable<T> extends Enumerable<T> {
    private final Iterable<T> iterable;

    public IterableEnumerable(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    @Override
    public Enumerator<T> enumerator() {
        return new IterableEnumerator<>(iterable.iterator());
    }

    @Override
    public Iterator<T> iterator() {
        return iterable.iterator();
    }
}
