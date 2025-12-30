package linq

fun <TSource> Sequence<TSource>.skipWhile(predicate: (TSource, Int) -> Boolean): Sequence<TSource> =
    sequence {
        val iterator = this@skipWhile.iterator()
        var index = -1
        while (iterator.hasNext()) {
            index++
            val element = iterator.next()
            if (!predicate(element, index)) {
                yield(element)
                while (iterator.hasNext()) {
                    yield(iterator.next())
                }
                return@sequence
            }
        }
    }

fun <TSource> Sequence<TSource>.skipLast(count: Int): Sequence<TSource> =
    if (count <= 0) this
    else sequence {
        val buffer = ArrayDeque<TSource>()
        val iterator = this@skipLast.iterator()

        while (iterator.hasNext()) {
            buffer.addLast(iterator.next())
            if (buffer.size > count) {
                yield(buffer.removeFirst())
            }
        }
    }