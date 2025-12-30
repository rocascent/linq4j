package linq

class LookUp<TKey, TElement> internal constructor(
    private val map: Map<TKey, List<TElement>>
) : Iterable<Group<TKey, TElement>> {
    val count: Int get() = map.size

    operator fun get(key: TKey): Enumerable<TElement> = Enumerable(map[key]?.asSequence() ?: emptySequence())

    fun contains(key: TKey): Boolean = map.containsKey(key)

    override fun iterator(): Iterator<Group<TKey, TElement>> {
        return map.asSequence().map { Group(it.key, Enumerable(it.value.asSequence())) }.iterator()
    }
}

fun <TSource, TKey> Sequence<TSource>.toLookUp(keySelector: (TSource) -> TKey): LookUp<TKey, TSource> =
    LookUp(this.groupByTo(mutableMapOf(), keySelector))

fun <TSource, TKey, TElement> Sequence<TSource>.toLookUp(
    keySelector: (TSource) -> TKey,
    elementSelector: (TSource) -> TElement
): LookUp<TKey, TElement> = LookUp(this.groupByTo(mutableMapOf(), keySelector, elementSelector))