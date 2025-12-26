package linq

abstract class AbstractIterator<TSource> : Enumerable<TSource>(), Enumerator<TSource> {
    private val threadId = Thread.currentThread().threadId()
    protected var state = 0
    protected var currentField: TSource? = null

    override val current: TSource get() = currentField ?: throw IllegalStateException()

    protected abstract fun clone(): AbstractIterator<TSource>

    override fun close() {
        currentField = null
        state = -1
    }

    override fun enumerator(): Enumerator<TSource> {
        val enumerator = if (state == 0 && threadId == Thread.currentThread().threadId()) this else clone()
        enumerator.state = 1
        return enumerator
    }

    abstract override fun moveNext(): Boolean

    open fun <TResult> itSelect(selector: (TSource) -> TResult): Enumerable<TResult> =
        EnumerableSelectIterator(this, selector)

    open fun itWhere(predicate: (TSource) -> Boolean): Enumerable<TSource> =
        EnumerableWhereIterator(this, predicate)

    //    @Override
    //    public void reset() {
    //        throw new UnsupportedOperationException()
    //    }

    abstract fun getList(): List<TSource>

    abstract fun itCount(): Int

    open fun itSkip(count: Int): AbstractIterator<TSource>? =
        EnumerableSkipTakeIterator(this, count, -1)


    open fun itTake(count: Int): AbstractIterator<TSource>? {
        return EnumerableSkipTakeIterator(this, 0, count - 1)
    }

    open fun tryGetElementAt(index: Int): TSource? =
        if (index == 0) {
            tryGetFirst()
        } else {
            //todo
            null
        }

    open fun tryGetFirst(): TSource? = tryGetFirstNonIterator(this)

    open fun tryGetLast(): TSource? = tryGetLastNonIterator(this)
}