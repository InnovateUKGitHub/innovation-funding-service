package org.innovateuk.ifs.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An immutable 4-tuple, akin to {@link org.apache.commons.lang3.tuple.Pair} or {@link org.apache.commons.lang3.tuple.Triple}
 */
public class Quad<T1, T2, T3, T4> {

    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;

    private Quad(T1 t1, T2 t2, T3 t3, T4 t4) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    public T1 get1() {
        return t1;
    }

    public T2 get2() {
        return t2;
    }

    public T3 get3() {
        return t3;
    }

    public T4 get4() {
        return t4;
    }

    public static <T1, T2, T3, T4> Quad<T1, T2, T3, T4> of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new Quad<>(t1, t2, t3, t4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Quad<?, ?, ?, ?> quad = (Quad<?, ?, ?, ?>) o;

        return new EqualsBuilder()
                .append(t1, quad.t1)
                .append(t2, quad.t2)
                .append(t3, quad.t3)
                .append(t4, quad.t4)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(t1)
                .append(t2)
                .append(t3)
                .append(t4)
                .toHashCode();
    }
}
