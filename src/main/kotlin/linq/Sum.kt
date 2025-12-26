package linq

import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import java.math.BigDecimal

fun <TSource> sum(source: Enumerable<TSource>?, selector: ((TSource) -> Int)?): Int {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    var sum = 0;
    for (value in source) {
        sum += selector(value);
    }

    return sum;
}

fun <TSource> sum(source: Enumerable<TSource>?, selector: ((TSource) -> Long)?): Long {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source);
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    var sum = 0L;
    for (value in source) {
        sum += selector(value);
    }

    return sum;
}

fun <TSource> sum(source: Enumerable<TSource>?, selector: ((TSource) -> Float)?): Float {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source);
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    var sum = 0f;
    for (value in source) {
        sum += selector(value);
    }

    return sum;
}

fun <TSource> sum(source: Enumerable<TSource>?, selector: ((TSource) -> Double)?): Double {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source);
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    var sum = 0.0;
    for (value in source) {
        sum += selector(value);
    }

    return sum;
}

fun <TSource> sum(source: Enumerable<TSource>?, selector: ((TSource) -> BigDecimal)?): BigDecimal {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.source);
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.selector)
    }

    var sum = BigDecimal.ZERO;
    for (value in source) {
        sum += selector(value);
    }

    return sum;
}

