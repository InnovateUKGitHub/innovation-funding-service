package com.worth.ifs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A base class from which concrete builders can extend
 *
 * Created by dwatson on 09/10/15.
 */
public abstract class BaseBuilder<T, S> implements Builder<T, S> {

    private final List<BiConsumer<Integer, T>> amendActions;

    // for factory method and with() use
    protected BaseBuilder(List<BiConsumer<Integer, T>> newActions) {
        this.amendActions = new ArrayList<>(newActions);
    }

    protected BaseBuilder() {
        this.amendActions = Collections.emptyList();
    }

    @Override
    public S with(Consumer<T> amendFunction) {
        List<BiConsumer<Integer, T>> newActions = new ArrayList<>(amendActions);
        newActions.add((i, t) -> amendFunction.accept(t));
        return createNewBuilderWithActions(newActions);
    }

    @Override
    public S with(BiConsumer<Integer, T> multiAmendFunction) {
        List<BiConsumer<Integer, T>> newActions = new ArrayList<>(amendActions);
        newActions.add(multiAmendFunction);
        return createNewBuilderWithActions(newActions);
    }

    public <R> S with(BiConsumer<R, T> amendFunction, R... values) {
        return with((i, t) -> amendFunction.accept(values[Math.min(values.length - 1, i)], t));
    }

    @Override
    public T build() {
        return build(1).get(0);
    }

    @Override
    public List<T> build(int numberToBuild) {

        return (List) IntStream.range(0, numberToBuild).mapToObj(i -> {
            T newElement = createInitial();
            amendActions.forEach(a -> a.accept(i, newElement));
            return newElement;
        }).collect(Collectors.toList());
    }

    protected abstract S createNewBuilderWithActions(List<BiConsumer<Integer, T>> actions);

    protected abstract T createInitial();
}
