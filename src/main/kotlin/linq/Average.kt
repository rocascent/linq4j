package linq

import linq.exception.ExceptionArgument
import linq.exception.throwArgumentNullException
import linq.exception.throwNoElementsException
import java.math.BigDecimal

fun <TSource> averageInt(source: Enumerable<TSource>?, selector: ((TSource) -> Int)?): Double {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.Selector)
    }

    source.enumerator().use {
        if (!it.moveNext()) {
            throwNoElementsException()
        }

        var sum = selector(it.current).toLong()
        var count = 1L

        while (it.moveNext()) {
            sum += selector(it.current).toLong()

            count++
        }

        return sum.toDouble() / count
    }
}

fun <TSource> averageLong(source: Enumerable<TSource>?, selector: ((TSource) -> Long)?): Double {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.Selector)
    }

    source.enumerator().use {
        if (!it.moveNext()) {
            throwNoElementsException()
        }

        var sum = selector(it.current)
        var count = 1L

        while (it.moveNext()) {
            sum += selector(it.current)

            count++
        }

        return sum.toDouble() / count
    }
}

fun <TSource> averageFloat(source: Enumerable<TSource>?, selector: ((TSource) -> Float)?): Float {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.Selector)
    }

    source.enumerator().use {
        if (!it.moveNext()) {
            throwNoElementsException()
        }

        var sum = selector(it.current)
        var count = 1L

        while (it.moveNext()) {
            sum += selector(it.current)

            count++
        }

        return sum / count
    }
}

fun <TSource> averageDouble(source: Enumerable<TSource>?, selector: ((TSource) -> Double)?): Double {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.Selector)
    }

    source.enumerator().use {
        if (!it.moveNext()) {
            throwNoElementsException()
        }

        var sum = selector(it.current)
        var count = 1L

        while (it.moveNext()) {
            sum += selector(it.current)

            count++
        }

        return sum / count
    }
}

fun <TSource> averageBigDecimal(source: Enumerable<TSource>?, selector: ((TSource) -> BigDecimal)?): BigDecimal {
    if (source == null) {
        throwArgumentNullException(ExceptionArgument.Source)
    }

    if (selector == null) {
        throwArgumentNullException(ExceptionArgument.Selector)
    }

    source.enumerator().use {
        if (!it.moveNext()) {
            throwNoElementsException()
        }

        var sum = selector(it.current)
        var count = 1L

        while (it.moveNext()) {
            sum += selector(it.current)

            count++
        }

        return sum / count.toBigDecimal()
    }
}