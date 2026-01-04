package linq

object Linq {
    @JvmStatic
    fun <TSource> of(): Enumerable<TSource> = Enumerable(emptySequence())

    @JvmStatic
    @SafeVarargs
    fun <TSource> of(vararg source: TSource): Enumerable<TSource> = Enumerable(source.asSequence())

    @JvmStatic
    fun of(source: ByteArray): Enumerable<Byte> = Enumerable(source.asSequence())

    @JvmStatic
    fun of(source: ShortArray): Enumerable<Short> = Enumerable(source.asSequence())

    @JvmStatic
    fun of(source: IntArray): Enumerable<Int> = Enumerable(source.asSequence())

    @JvmStatic
    fun of(source: LongArray): Enumerable<Long> = Enumerable(source.asSequence())

    @JvmStatic
    fun <TSource> of(source: Iterable<TSource>) = Enumerable(source.asSequence())
}