package linq;

public interface Enumerator<TSource> extends AutoCloseable {
    boolean moveNext();

    TSource current();

    @Override
    default void close() {
    }
}
