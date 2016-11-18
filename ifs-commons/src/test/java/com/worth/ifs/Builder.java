package com.worth.ifs;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Describes a builder for instances of a given type T, with handy functions for generating multiple unique instances
 * in a single build step.
 *
 * @param <T> - the type being built
 * @param <S> - the builder implementation's Self-type (i.e. the same class as the implementation class)
 */
public interface Builder<T, S> {

    /**
     * Given a consumer that is able to take an instance of T currently being built and altering it in some way
     * (e.g. setting a name on it), this method will return a new Builder with that consumer added to a queue of similar
     * consumers that will be executed in sequence to amend an instance of T one by one
     */
    S with(Consumer<T> amendFunction);

    /**
     * As per the with() method above, but with the addition of a handy index so that if we are generating multiple
     * instances in a single build step, we can get the index of the T currently being built and use the index to
     * apply some unique index-based value upon the T being built (e.g. by setting its name to ("Thing " + index)
     * to generate unique index-based names
     */
    S with(BiConsumer<Integer, T> amendFunction);

    /**
     * As per the with() method above, but with the addition of T's id value (if applicable), so that if we are generating
     * multiple instances in a single build step, we can get the id of the T currently being built and use the id to
     * apply some unique id-based value upon the T being built (e.g. by setting its name to ("Thing " + id) to generate
     * unique id-based names
     */
    S withIdBased(BiConsumer<Long, T> amendFunction);

    /**
     * Builds a single instance of T, by firstly creating a new T and then applying all consumers that had been added to
     * this Builder one by one
     */
    T build();

    /**
     * As per the above build() method, but generates multiple instances in a single step and returns the results as a
     * List
     */
    List<T> build(int numberToBuild);

    /**
     * As per the above build(int) method, but returns the instances in an array for the convenience of array-based
     * methods
     */
    T[] buildArray(int numberToBuild, Class<T> clazz);

    /**
     * As per the above build(int) method, but returns the instances in a Set for the convenience of Set-based
     * methods
     */
    Set<T> buildSet(int numberToBuild);
}
