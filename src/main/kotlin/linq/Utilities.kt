package linq

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

typealias EqualityComparer<T> = (T, T) -> Boolean

@OptIn(ExperimentalContracts::class)
inline fun <T> T?.assertNotNull(
    lazyMessage: () -> String = { "" }
): T {
    contract {
        returns() implies (this@assertNotNull != null)
    }

    return this ?: throw AssertionError(lazyMessage())
}

infix fun <TSource> ((TSource) -> Boolean).and(other: (TSource) -> Boolean): (TSource) -> Boolean {
    return { this(it) && other(it) }
}

infix fun <TSource, TMiddle, TResult> ((TSource) -> TMiddle).then(other: (TMiddle) -> TResult): (TSource) -> TResult {
    return { other(this(it)) }
}