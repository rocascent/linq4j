package linq

fun <TSource> Sequence<TSource>.sum(selector: (TSource) -> Float): Float {
    var sum: Float = 0.toFloat()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
