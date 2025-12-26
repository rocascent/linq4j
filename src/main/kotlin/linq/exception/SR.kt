package linq.exception

object SR {
    const val Arg_ArgumentException = "Value does not fall within the expected range."
    const val Arg_ParamName_Name = "Parameter name: %s."
    const val ArgumentNull_Generic = "Value cannot be null."
    const val Arg_ArgumentOutOfRangeException = "Specified argument was out of the range of valid values."
    const val ArgumentOutOfRange_ActualValue = "Actual value was %s."
    const val Arg_InvalidOperationException = "Operation is not valid due to the current state of the object."
    const val Arg_NotSupportedException = "Specified method is not supported."
    const val Arg_RepeatInvokeException = "Specified method cannot be invoked repeatedly."
    const val InvalidOperation_EmptyQueue = "Queue empty."

    const val EmptyIterable = "Iteration yielded no results."
    const val EmptyEnumerable = "Enumeration yielded no results."
    const val MoreThanOneElement = "Sequence contains more than one element."
    const val MoreThanOneMatch = "Sequence contains more than one matching element."
    const val NO_ELEMENTS = "Sequence contains no elements."
    const val NO_MATCH = "Sequence contains no matching element."
    const val NoSuchElement = "Sequence contains no such element."
    const val Arg_IndexOutOfRangeException = "Index was outside the bounds of the array."
    const val Argument_ImplementComparable = "At least one object must implement Comparable."
    const val ArgumentException_TupleIncorrectType = "Argument must be of type %s."
    const val ArgumentException_TupleLastArgumentNotATuple =
        "The last element of an eight element tuple must be a Tuple."
}