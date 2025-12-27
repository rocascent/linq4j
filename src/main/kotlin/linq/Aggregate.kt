package linq

import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import linq.exception.throwNoElementsException

fun <TSource> aggregate(source: Enumerable<TSource>?, func: ((TSource, TSource) -> TSource)?): TSource {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (func == null) {
        throwArgumentNullException(ExceptionArgument.Func)
    }

    source.enumerator().use {
        if (!it.moveNext()) {
            throwNoElementsException()
        }

        var result = it.current
        while (it.moveNext()) {
            result = func(result, it.current)
        }

        return result
    }
}

fun <TSource, TAccumulate> aggregate(
    source: Enumerable<TSource>?,
    seed: TAccumulate,
    func: ((TAccumulate, TSource) -> TAccumulate)?
): TAccumulate {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (func == null) {
        throwArgumentNullException(ExceptionArgument.Func)
    }

    return source.fold(seed, func)
}

fun <TSource, TAccumulate, TResult> aggregate(
    source: Enumerable<TSource>?,
    seed: TAccumulate,
    func: ((TAccumulate, TSource) -> TAccumulate)?,
    resultSelector: ((TAccumulate) -> TResult)?
): TResult {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (func == null) {
        throwArgumentNullException(ExceptionArgument.Func)
    }

    if (resultSelector == null) {
        throwArgumentNullException(ExceptionArgument.ResultSelector)
    }

    return resultSelector(source.fold(seed, func))
}