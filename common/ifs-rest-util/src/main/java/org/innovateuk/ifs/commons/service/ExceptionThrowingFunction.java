package org.innovateuk.ifs.commons.service;

/**
 * A functional interface that represents a Function that can throw Exceptions
 */
@FunctionalInterface
public interface ExceptionThrowingFunction<T, R> {

    R apply(T object) throws Exception;
}
