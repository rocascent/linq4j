package linq.exception

fun throwArgumentNullException(argument: ExceptionArgument): Nothing {
    throw NullPointerException(argument.string + " is null")
}

fun throwArgumentOutOfRangeException(argument: ExceptionArgument): Nothing {
    throw IllegalArgumentException(argument.string + " is out of range")
}

fun throwNoElementsException(): Nothing {
    throw IllegalStateException(SR.NO_ELEMENTS)
}

fun throwNoMatchException(): Nothing {
    throw IllegalStateException(SR.NO_MATCH)
}