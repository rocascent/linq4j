package linq

fun <TSource> Sequence<TSource>.takeWhile(predicate: (TSource, Int) -> Boolean): Sequence<TSource> =
    sequence {
        var index = -1
        for (element in this@takeWhile) {
            index = Math.addExact(index, 1)

            if (!predicate(element, index)) {
                break
            }

            yield(element)
        }
    }

fun <TSource> Sequence<TSource>.takeLast(count: Int): Sequence<TSource> =
    if (count <= 0) this
    else sequence {
        val buffer = ArrayDeque<TSource>()
        val iterator = this@takeLast.iterator()

        while (iterator.hasNext()) {
            buffer.addLast(iterator.next())
            if (buffer.size > count) {
                buffer.removeFirst()
            }
        }

        buffer.forEach { yield(it) }
    }