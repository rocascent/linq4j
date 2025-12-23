package linq;

import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

import java.math.BigDecimal;
import java.util.function.Function;

public class Sum {
    static <TSource> int sumInt(Enumerable<TSource> source, Function<TSource, Integer> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        int sum = 0;
        for (var value : source) {
            sum += selector.apply(value);
        }

        return sum;
    }

    static <TSource> long sumLong(Enumerable<TSource> source, Function<TSource, Long> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        long sum = 0;
        for (var value : source) {
            sum += selector.apply(value);
        }

        return sum;
    }

    static <TSource> float sumFloat(Enumerable<TSource> source, Function<TSource, Float> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        float sum = 0;
        for (var value : source) {
            sum += selector.apply(value);
        }

        return sum;
    }

    static <TSource> double sumDouble(Enumerable<TSource> source, Function<TSource, Double> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        double sum = 0;
        for (var value : source) {
            sum += selector.apply(value);
        }

        return sum;
    }

    static <TSource> BigDecimal sumBigDecimal(Enumerable<TSource> source, Function<TSource, BigDecimal> selector) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (var value : source) {
            sum = sum.add(selector.apply(value));
        }

        return sum;
    }
}
