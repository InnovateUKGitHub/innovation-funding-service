package org.innovateuk.ifs.util;

/**
 * A functional interface that represents a Runnable that can throw Exceptions
 */
@FunctionalInterface
public interface ExceptionThrowingRunnable {

    void run() throws Exception;
}
