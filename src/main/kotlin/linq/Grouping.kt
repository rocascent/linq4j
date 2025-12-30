package linq

data class Group<TKey, TElement>(
    val key: TKey,
    val elements: Enumerable<TElement>
)

fun <TSource, TKey> Sequence<TSource>.groupBy(keySelector: (TSource) -> TKey): Sequence<Group<TKey, TSource>> =
    this.toLookUp(keySelector).asSequence()

fun <TSource, TKey, TElement> Sequence<TSource>.groupBy(
    keySelector: (TSource) -> TKey,
    elementSelector: (TSource) -> TElement
): Sequence<Group<TKey, TElement>> = this.toLookUp(keySelector, elementSelector).asSequence()