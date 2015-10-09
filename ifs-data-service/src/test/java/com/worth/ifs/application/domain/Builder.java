package com.worth.ifs.application.domain;

import java.util.function.Consumer;

/**
 * Created by dwatson on 07/10/15.
 */
public interface Builder<T> {

    Builder<T> with(Consumer<T> amendFunction);

    T build();
}
