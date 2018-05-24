package org.innovateuk.ifs.util;

/**
 * A consumer that takes 4 arguments
 */
@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {

    void accept(A a, B b, C c, D d);
}
