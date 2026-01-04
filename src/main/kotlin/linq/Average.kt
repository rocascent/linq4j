package linq

import java.math.BigDecimal

fun Sequence<BigDecimal>.average(): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    var count = 0
    for (element in this) {
        sum += element
        count = Math.addExact(count, 1)
    }
    return sum / BigDecimal(count)
}