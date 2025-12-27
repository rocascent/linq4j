package linq

import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException

fun <TSource> toList(source: Enumerable<TSource>?): List<TSource> {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (source is AbstractIterator<TSource>) {
        return source.getList()
    }

    val list = arrayListOf<TSource>()
    for (item in source) {
        list.add(item)
    }
    return source.toMutableList()
}
