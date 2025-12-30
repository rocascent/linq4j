package linq

fun <TSource, TResult> Sequence<TSource>.selectMany(selector: (TSource) -> Iterable<TResult>): Sequence<TResult> =
    sequence {
        for (element in this@selectMany) {
            for (subElement in selector(element)) {
                yield(subElement)
            }
        }
    }

fun <TSource, TResult> Sequence<TSource>.selectMany(selector: (TSource, Int) -> Iterable<TResult>): Sequence<TResult> =
    sequence {
        var index = -1
        for (element in this@selectMany) {
            index++
            for (subElement in selector(element, index)) {
                yield(subElement)
            }
        }
    }

fun <TSource, TCollection, TResult> Sequence<TSource>.selectMany(
    collectionSelector: (TSource) -> Iterable<TCollection>,
    resultSelector: (TSource, TCollection) -> TResult
): Sequence<TResult> = sequence {
    for (element in this@selectMany) {
        for (subElement in collectionSelector(element)) {
            yield(resultSelector(element, subElement))
        }
    }
}

fun <TSource, TCollection, TResult> Sequence<TSource>.selectMany(
    collectionSelector: (TSource, Int) -> Iterable<TCollection>,
    resultSelector: (TSource, TCollection) -> TResult
): Sequence<TResult> = sequence {
    var index = -1
    for (element in this@selectMany) {
        index++
        for (subElement in collectionSelector(element, index)) {
            yield(resultSelector(element, subElement))
        }
    }
}