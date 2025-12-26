package linq

import linq.collections.enumerable.ArrayEnumerable

fun <TSource> tryGetLastNonIterator(source: Enumerable<TSource>): TSource? {
    if (source is ArrayEnumerable<TSource>) {
        val count = source.size
        if (count > 0) {
            return source[count - 1]
        }
    } else {
        source.enumerator().use {

            if (it.moveNext()) {
                var result: TSource
                do {
                    result = it.current
                } while (it.moveNext())

                return result
            }
        }
    }

    return null
}