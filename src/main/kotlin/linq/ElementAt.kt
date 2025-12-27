package linq

fun <TSource> tryGetElementAtNonIterator(source: Enumerable<TSource>, index: Int): TSource? {
    var index = index
    if (index >= 0) {
        source.enumerator().use {
            while (it.moveNext()) {
                if (index == 0) {
                    return it.current
                }
                index--
            }
        }
    }

    return null
}