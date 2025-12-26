package linq

interface Enumerator<out TSource> : AutoCloseable {
    val current: TSource

    fun moveNext(): Boolean

    override fun close() {
    }
}
