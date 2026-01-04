package linq

fun <TFirst, TSecond, TResult> Sequence<TFirst>.zip(
    other: Iterable<TSecond>,
    resultSelector: (TFirst, TSecond) -> TResult
): Sequence<TResult> = sequence {
    val i1 = this@zip.iterator()
    val i2 = other.iterator()
    while (i1.hasNext() && i2.hasNext()) {
        yield(resultSelector(i1.next(), i2.next()))
    }
}

fun <TFirst, TSecond> Sequence<TFirst>.zip(other: Iterable<TSecond>): Sequence<Tuple<TFirst, TSecond>> = sequence {
    val i1 = this@zip.iterator()
    val i2 = other.iterator()
    while (i1.hasNext() && i2.hasNext()) {
        yield(Tuple(i1.next(), i2.next()))
    }
}