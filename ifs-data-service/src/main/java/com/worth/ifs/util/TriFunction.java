package com.worth.ifs.util;

/**
 * A function that takes 3 arguments and produces an output
 */
@FunctionalInterface
public interface TriFunction<A, B, C, T> {

    T apply(A a, B b, C c);
}
