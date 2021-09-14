package org.innovateuk.ifs.async.exceptions;

import java.util.function.Supplier;

/**
 * An exception that can be used as a wrapper exception to allow us to throw RuntimeExceptions when we have
 * failures within async code blocks.  This allows us to unwrap root cause exceptions when receiving exceptions
 * from nested async handlers.
 *
 * Note that this Exception class is used within the async mechanism supplied in the {@link org.innovateuk.ifs.async}
 * package for wrapping and rethrowing exceptions and is best not to be used manually by general production code.
 */
public class AsyncException extends RuntimeException {

    public AsyncException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Method that, given a Throwable, will return the same Throwable if it is an AsyncException or wrap it within
     * an AsyncException if it is not
     */
    public static AsyncException getOriginalAsyncExceptionOrWrapInAsyncException(Throwable e, Supplier<String> wrappingMessageSupplier) {

        if (e instanceof AsyncException) {
            throw (AsyncException) e;
        }

        if (e.getCause() instanceof AsyncException) {
            return (AsyncException) e.getCause();
        }

        throw new AsyncException(wrappingMessageSupplier.get(), e);
    }

    /**
     * Method to find the original Throwable when potentially wrapped in AsyncExceptions.
     */
    public static Throwable unwrapOriginalExceptionFromAsyncException(Throwable e) {

        if (e.getCause() == null) {
            return e;
        }

        if (!(e instanceof AsyncException)) {
            return e;
        }

        return unwrapOriginalExceptionFromAsyncException(e.getCause());
    }
}
