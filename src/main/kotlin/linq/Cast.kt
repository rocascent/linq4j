package linq

fun <TSource, TResult> Sequence<TSource>.cast(clazz: Class<TResult>): Sequence<TResult> =
    this.map { clazz.cast(it) }