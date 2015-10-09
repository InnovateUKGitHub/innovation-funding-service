package com.worth.ifs;

import com.worth.ifs.application.domain.Response;
import org.apache.commons.lang3.NotImplementedException;

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
public abstract class BaseBuilder<T> implements Builder<T> {

    private final List<Consumer<T>> amendActions;
    private final List<BiConsumer<Integer, T>> multiAmendActions;

    // for factory method and with() use
    protected BaseBuilder(List<Consumer<T>> newActions, List<BiConsumer<Integer, T>> newMultiActions) {
        this.amendActions = new ArrayList<>(newActions);
        this.multiAmendActions = new ArrayList<>(newMultiActions);
    }

    protected BaseBuilder() {
        this.amendActions = Collections.emptyList();
        this.multiAmendActions = Collections.emptyList();
    }

    @Override
    public <R extends Builder<T>> R with(Consumer<T> amendFunction) {
        List<Consumer<T>> newActions = new ArrayList<>(amendActions);
        newActions.add(amendFunction);
        return (R) createNewBuilderWithActions(newActions, multiAmendActions);
    }

    @Override
    public <R extends Builder<T>> R with(BiConsumer<Integer, T> multiAmendFunction) {
        List<BiConsumer<Integer, T>> newActions = new ArrayList<>(multiAmendActions);
        newActions.add(multiAmendFunction);
        return (R) createNewBuilderWithActions(amendActions, newActions);
    }

    @Override
    public T build() {
        return build(1).get(0);
    }

    @Override
    public List<T> build(int numberToBuild) {

        return (List) IntStream.range(0, numberToBuild).mapToObj(i -> {
            T newElement = createInitial();
            amendActions.forEach(a -> a.accept(newElement));
            multiAmendActions.forEach(a -> a.accept(i, newElement));
            return newElement;
        }).collect(Collectors.toList());
    }

    protected abstract BaseBuilder<T> createNewBuilderWithActions(List<Consumer<T>> actions, List<BiConsumer<Integer, T>> multiActions);

    protected abstract T createInitial();
}
