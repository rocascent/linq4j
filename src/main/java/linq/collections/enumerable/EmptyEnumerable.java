package linq.collections.enumerable;

import linq.Enumerable;
import linq.Enumerator;
import linq.collections.enumerator.EmptyEnumerator;

public class EmptyEnumerable<T> extends Enumerable<T> {
    @Override
    public Enumerator<T> enumerator() {
        return new EmptyEnumerator<>();
    }
}
