package com.worth.ifs.util;

import java.io.IOException;

/**
 * A consumer that takes 3 arguments
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void apply(A a, B b, C c) throws IOException;
}
