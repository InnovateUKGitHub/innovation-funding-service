package com.worth.ifs;

import java.util.function.Consumer;

/**
 * Created by dwatson on 07/10/15.
 */
public interface Builder<T> {

    <R extends Builder<T>> R with(Consumer<T> amendFunction);

    T build();
}
