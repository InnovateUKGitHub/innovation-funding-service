package com.worth.ifs.commons.service;

/**
 *
 */
@FunctionalInterface
public interface ExceptionThrowingFunction<T, R> {

    R apply(T object) throws Exception;
}