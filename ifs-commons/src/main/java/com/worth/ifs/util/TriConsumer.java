package com.worth.ifs.util;

/**
 * A consumer that takes 3 arguments
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A a, B b, C c);
}
