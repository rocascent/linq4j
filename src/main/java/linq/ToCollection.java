package linq;

import linq.exception.ExceptionArgument;
import linq.exception.ThrowHelper;

import java.util.ArrayList;
import java.util.List;

public class ToCollection {
    static <TSource> List<TSource> toList(Enumerable<TSource> source) {
        if (source == null) {
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        }

        if(source instanceof AbstractIterator<TSource> iterator) {
            return iterator.toList();
        }

        var list = new ArrayList<TSource>();
        var iterator = source.iterator();
        iterator.forEachRemaining(list::add);
        return list;
    }
}
