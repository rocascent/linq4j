package linq;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractIterator<TSource> extends Enumerable<TSource> implements Enumerator<TSource> {
    private final long threadId = Thread.currentThread().threadId();
    protected int state = 0;
    protected TSource current = null;

    @Override
    public TSource current() {
        return current;
    }

    protected abstract AbstractIterator<TSource> getClone();

    @Override
    public void close() {
        current = null;
        state = -1;
    }

    @Override
    public Enumerator<TSource> enumerator() {
        var enumerator = state == 0 && threadId == Thread.currentThread().threadId() ? this : getClone();
        enumerator.state = 1;
        return enumerator;
    }

    public abstract boolean moveNext();


    public <TResult> Enumerable<TResult> select(Function<TSource, TResult> selector) {
        return new Select.EnumerableSelectIterator<>(this, selector);
    }

    public Enumerable<TSource> where(Predicate<TSource> predicate) {
        return new Where.EnumerableWhereIterator<>(this, predicate);
    }

    //    @Override
    //    public void reset() {
    //        throw new UnsupportedOperationException();
    //    }

    public abstract List<TSource> toList();

    public abstract int getCount();

    public AbstractIterator<TSource> skip(int count) {
        return new SkipTake.EnumerableSkipTakeIterator<>(this, count, -1);
    }

    public AbstractIterator<TSource> take(int count) {
        return new SkipTake.EnumerableSkipTakeIterator<>(this, 0, count - 1);
    }

    public TSource tryGetFirst(boolean[] found) {
        return First.tryGetFirstNonIterator(this, found);
    }
}