package linq.exception;

public class ThrowHelper {
    public static void throwArgumentNullException(ExceptionArgument argument) {
        throw new NullPointerException(argument.name() + " is null");
    }

    public static void throwNoElementsException() {
        throw new IllegalStateException(SR.NoElements);
    }

    public static void throwNoMatchException() {
        throw new IllegalStateException(SR.NoMatch);
    }
}
