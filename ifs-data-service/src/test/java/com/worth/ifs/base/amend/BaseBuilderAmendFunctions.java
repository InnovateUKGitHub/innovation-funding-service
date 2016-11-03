package com.worth.ifs.base.amend;

import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * A set of functions that can be used by Builders to set fields.
 */
public class BaseBuilderAmendFunctions {

    private static Long nextId = 1L;

    public static void clearUniqueIds() {
        nextId = 1L;
    }

    public static <T> Consumer<T> id(Long id) {
        return t -> setId(id, t);
    }

    public static <T> Consumer<T> name(String value) {
        return t -> setName(value, t);
    }

    public static <T> Consumer<T> description(String value) {
        return t -> setDescription(value, t);
    }

    public static <T> BiConsumer<Integer, T> incrementingIds() {
        return incrementingIds(0);
    }

    public static <T> BiConsumer<Integer, T> uniqueIds() {
        return (i, t) -> setId(nextId++, t);
    }

    public static <T> Consumer<T> idBasedNames(String prefix) {
        return names(t -> prefix + getId(t));
    }

    public static <T> Consumer<T> idBasedDescriptions(String prefix) {
        return descriptions(t -> prefix + getId(t));
    }

    public static <T> Consumer<T> idBasedValues(String prefix) {
        return values(t -> prefix + getId(t));
    }

    public static <T> Consumer<T> idBasedTitles(String prefix) {
        return titles(t -> prefix + getId(t));
    }

    public static <T> Consumer<T> idBasedTypes(String prefix) {
        return types(t -> prefix + getId(t));
    }

    public static <T> BiConsumer<Integer, T> incrementingIds(int fromInclusive) {
        return (i, t) -> setId((long) i + fromInclusive, t);
    }

    public static <T> BiConsumer<Integer, T> names(BiFunction<Integer, T, String> nameGenerationFunction) {
        return (i, t) -> setName(nameGenerationFunction.apply(i, t), t);
    }

    public static <T> BiConsumer<Integer, T> names(String... names) {
        return (i, t) -> setName(names[i], t);
    }

    public static <T> Consumer<T> names(Function<T, String> nameGenerationFunction) {
        return t -> setName(nameGenerationFunction.apply(t), t);
    }

    public static <T> Consumer<T> descriptions(Function<T, String> nameGenerationFunction) {
        return t -> setDescription(nameGenerationFunction.apply(t), t);
    }

    public static <T> Consumer<T> values(Function<T, String> nameGenerationFunction) {
        return t -> setValue(nameGenerationFunction.apply(t), t);
    }

    public static <T> Consumer<T> titles(Function<T, String> nameGenerationFunction) {
        return t -> setTitle(nameGenerationFunction.apply(t), t);
    }

    public static <T> Consumer<T> types(Function<T, String> nameGenerationFunction) {
        return t -> setType(nameGenerationFunction.apply(t), t);
    }

    public static <T> Long getId(T instance) {
        return (Long) getField(instance, "id");
    }

    public static <T> T setId(Long value, T instance) {
        return setField("id", value, instance);
    }

    public static <T> T setCompetition(Long value, T instance) {
        return setField("competition", value, instance);
    }

    public static <T> T setName(String value, T instance) {
        return setField("name", value, instance);
    }

    public static <T> T setDescription(Object value, T instance) {
        return setField("description", value, instance);
    }

    public static <T> T setValue(Object value, T instance) {
        return setField("value", value, instance);
    }

    public static <T> T setTitle(Object value, T instance) {
        return setField("title", value, instance);
    }

    public static <T> T setType(Object value, T instance) {
        return setField("type", value, instance);
    }

    public static <T> T setField(String fieldName, Object value, T instance) {
        try {
            ReflectionTestUtils.invokeSetterMethod(instance, fieldName, value);
        } catch (Exception e) {
            ReflectionTestUtils.setField(instance, fieldName, value);
        }
        return instance;
    }

    public static <T> T addToList(String fieldName, Object value, T instance) {

        List<Object> existingList = (List<Object>) getField(instance, fieldName);
        List<Object> newList = new ArrayList<>();

        if (existingList != null) {
            newList.addAll(existingList);
        }

        newList.add(value);

        return setField(fieldName, newList, instance);
    }

    public static <T> T addListToList(String fieldName, List<?> value, T instance) {

        List<Object> existingList = (List<Object>) getField(instance, fieldName);
        List<Object> newList = new ArrayList<>();

        if (existingList != null) {
            newList.addAll(existingList);
        }

        newList.addAll(value);

        return setField(fieldName, newList, instance);
    }

    public static <T> T createDefault(Class<T> clazz) {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new IllegalStateException("Attempt to invoke non-existent default constructor on " + clazz.getName());
        }
    }

    public static Function<Integer, Integer> zeroBasedIndexes() {
        return Function.identity();
    }

    public static Function<Integer, Integer> oneBasedIndexes() {
        return index -> index + 1;
    }
}
