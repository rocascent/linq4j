package linq

public data class IndexedElement<T>(public val index: Int, public val element: T)

fun <TSource> Sequence<TSource>.index(): Sequence<IndexedElement<TSource>> = sequence {
    var index = -1;
    for (element in this@index) {
        index = Math.addExact(index, 1)

        yield(IndexedElement(index, element))
    }
}