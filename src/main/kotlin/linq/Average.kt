package linq

import java.math.BigDecimal

fun Sequence<BigDecimal>.average(): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    var count = 0
    for (element in this) {
        sum += element
        ++count
        if (count < 0) {
            throw ArithmeticException("Count overflow has happened.")
        }
    }
    return if (count == 0) throw NumberFormatException() else sum / BigDecimal(count)
}