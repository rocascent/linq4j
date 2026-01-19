package linq

import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlin.streams.asSequence

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

    @JvmStatic
    fun <TSource> of(source: Stream<TSource>) = Enumerable(source.asSequence())

    @JvmStatic
    fun <TSource> of(source: IntStream) = Enumerable(source.asSequence())

    @JvmStatic
    fun <TSource> of(source: LongStream) = Enumerable(source.asSequence())

    @JvmStatic
    fun <TSource> of(source: DoubleStream) = Enumerable(source.asSequence())
}