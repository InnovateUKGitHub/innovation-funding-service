package org.innovateuk.ifs.util;

/**
 * A function that takes 4 arguments and produces an output
 */
@FunctionalInterface
public interface QuadFunction<A, B, C, D, T> {

    T apply(A a, B b, C c, D d);
}
