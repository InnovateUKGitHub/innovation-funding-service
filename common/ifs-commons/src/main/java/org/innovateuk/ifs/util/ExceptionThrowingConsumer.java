package org.innovateuk.ifs.util;

/**
 * A functional interface that represents a Consumer that can throw Exceptions
 */
@FunctionalInterface
public interface ExceptionThrowingConsumer<T> {

    void accept(T accept) throws Exception;
}
