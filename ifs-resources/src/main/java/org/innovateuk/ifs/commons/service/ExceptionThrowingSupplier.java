package org.innovateuk.ifs.commons.service;

/**
 * A functional interface that represents a Supplier that can throw Exceptions
 */
@FunctionalInterface
public interface ExceptionThrowingSupplier<R> {

    R get() throws Exception;
}
