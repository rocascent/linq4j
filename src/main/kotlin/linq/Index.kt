package linq


fun <TSource> Sequence<TSource>.index(): Sequence<Tuple<Int, TSource>> = sequence {
    var index = -1
    for (element in this@index) {
        index = Math.addExact(index, 1)

        yield(Tuple(index, element))
    }
}