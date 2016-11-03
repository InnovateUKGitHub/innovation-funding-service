package com.worth.ifs;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.getId;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

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

    @Override
    public S withIdBased(BiConsumer<Long, T> multiAmendFunction) {
        BiConsumer<Integer, T> wrapperFunction = (i, t) -> {
            Long id = getId(t);
            multiAmendFunction.accept(id, t);
        };
        return with(wrapperFunction);
    }

    public <R> S withArray(BiConsumer<R, T> amendFunction, R[] values) {
        return with((i, t) -> {
            R nextValue = values != null && values.length > 0 ? values[Math.min(values.length - 1, i)] : null;
            amendFunction.accept(nextValue, t);
        });
    }

    /**
     * Given a List of values, this function will supply each item in the list to the BiConsumer provided, so that you
     * can use each item in the List and supply it to each entity you're creating with this builder.  If you're creating
     * 3 entities with this builder (builder.build(3)) and you supply a List with 3 items in it, item 1 will be provided
     * to entity 1, item 2 to entity 2 etc
     *
     * @param values
     * @param amendFunction
     * @param <R>
     * @return
     */
    public <R> S withList(List<R> values, BiConsumer<R, T> amendFunction) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values list should contain at least one value");
        }
        return with((i, t) -> amendFunction.accept(values.get(Math.min(values.size() - 1, i)), t));
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
            postProcess(i, newElement);
            return newElement;
        }).collect(toList());
    }

    @Override
    public Set<T> buildSet(int numberToBuild) {
        return new LinkedHashSet<>(build(numberToBuild));
    }

    @Override
    public T[] buildArray(int numberToBuild, Class<T> clazz) {
        build(numberToBuild);
        return build(numberToBuild).toArray((T[]) Array.newInstance(clazz, numberToBuild));
    }

    protected <T> T newInstance(Class<T> clazz) {
        try {
            Optional<? extends Constructor<?>> ctor =
                    stream(clazz.getDeclaredConstructors()).filter(c -> c.getParameters().length == 0).findFirst();

            if (!ctor.isPresent()) {
                throw new RuntimeException("No zero-args constructor for class " + clazz);
            }

            Constructor<T> accessibleConstructor = (Constructor<T>) ctor.get();
            ReflectionUtils.makeAccessible(accessibleConstructor);
            return accessibleConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to instantiate new instance of class " + clazz, e);
        }
    }

    /**
     * Give subclasses of this BaseBuilder the chance to post-process any built instances prior to returning them.
     * An example of post-processing them could be adding Hibernate-style backlinks to objects within the new instance
     */
    protected void postProcess(int index, T instance) {
        // by default, do nothing
    }

    protected abstract S createNewBuilderWithActions(List<BiConsumer<Integer, T>> actions);

    protected abstract T createInitial();
}
