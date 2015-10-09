package com.worth.ifs;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by dwatson on 07/10/15.
 */
public interface Builder<T> {

    <R extends Builder<T>> R with(Consumer<T> amendFunction);

    <R extends Builder<T>> R with(BiConsumer<Integer, T> amendFunction);

    T build();

    List<T> build(int numberToBuild);
}
