package com.worth.ifs;

import com.worth.ifs.application.domain.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A base class from which concrete builders can extend
 *
 * Created by dwatson on 09/10/15.
 */
public abstract class BaseBuilder<T> implements Builder<T> {

    protected final List<Consumer<T>> amendActions;

    // for factory method and with() use
    protected BaseBuilder(List<Consumer<T>> newActions) {
        this.amendActions = new ArrayList<>(newActions);
    }

    protected BaseBuilder() {
        this.amendActions = Collections.emptyList();
    }

    @Override
    public <R extends Builder<T>> R with(Consumer<T> amendFunction) {
        List<Consumer<T>> newActions = new ArrayList<>(amendActions);
        newActions.add(amendFunction);
        return (R) createNewBuilderWithActions(newActions);
    }

    @Override
    public T build() {
        T toBuild = createInitial();
        amendActions.forEach(a -> a.accept(toBuild));
        return toBuild;
    }

    protected abstract BaseBuilder<T> createNewBuilderWithActions(List<Consumer<T>> actions);

    protected abstract T createInitial();
}
