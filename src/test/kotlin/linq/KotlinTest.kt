package linq

import org.junit.jupiter.api.Test

class KotlinTest {
    @Test
    fun test() {
        fibonacciSequence().toList()
        fibonacciSequence().filter { it > 5 }.take(5).forEach { println(it) }
    }

    fun fibonacciSequence(): Sequence<Int> = sequence {
        var a = 0
        var b = 1
        while (true) {
            yield(a) // 等价于 C# 的 yield return a
            val next = a + b
            a = b
            b = next
        }
    }
}