package linq

fun <TSource, TResult> Sequence<TSource>.ofType(clazz: Class<TResult>): Sequence<TResult> = sequence {
    for (element in this@ofType) {
        if (clazz.isInstance(element)) {
            yield(clazz.cast(element))
        }
    }
}