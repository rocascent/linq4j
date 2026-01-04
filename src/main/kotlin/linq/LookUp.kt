package linq

interface LookUp<TKey, TElement> : Iterable<Group<TKey?, TElement>> {
    operator fun get(key: TKey): Enumerable<TElement>

    fun contains(key: TKey): Boolean

    override fun iterator(): Iterator<Group<TKey?, TElement>>
}

internal class MapLookUp<TKey, TElement>(
    private val map: Map<TKey?, List<TElement>>
) : LookUp<TKey, TElement> {
    override operator fun get(key: TKey): Enumerable<TElement> = Enumerable(map[key]?.asSequence() ?: emptySequence())

    override fun contains(key: TKey): Boolean = map.containsKey(key)

    fun getGrouping(key: TKey): List<TElement>? = if (contains(key)) map[key] else null

    override fun iterator(): Iterator<Group<TKey?, TElement>> {
        return map.asSequence().map { Group(it.key, Enumerable(it.value.asSequence())) }.iterator()
    }
}

internal fun <TSource, TKey> createLoopUp(
    source: Sequence<TSource>,
    keySelector: (TSource) -> TKey?
): MapLookUp<TKey, TSource> = MapLookUp(source.groupByTo(mutableMapOf(), keySelector))

internal fun <TSource, TKey, TElement> createLoopUp(
    source: Sequence<TSource>,
    keySelector: (TSource) -> TKey?,
    elementSelector: (TSource) -> TElement
): MapLookUp<TKey, TElement> = MapLookUp(source.groupByTo(mutableMapOf(), keySelector, elementSelector))

fun <TSource, TKey> Sequence<TSource>.toLookUp(keySelector: (TSource) -> TKey?): LookUp<TKey, TSource> =
    createLoopUp(this, keySelector)

fun <TSource, TKey, TElement> Sequence<TSource>.toLookUp(
    keySelector: (TSource) -> TKey?,
    elementSelector: (TSource) -> TElement
): LookUp<TKey, TElement> = createLoopUp(this, keySelector, elementSelector)