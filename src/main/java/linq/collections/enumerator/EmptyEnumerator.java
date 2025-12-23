package linq.collections.enumerator;

import linq.Enumerator;

public class EmptyEnumerator<T> implements Enumerator<T> {
    @Override
    public boolean moveNext() {
        return false;
    }

    @Override
    public T current() {
        return null;
    }
}
