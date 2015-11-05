package com.worth.ifs;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * Created by dwatson on 09/10/15.
 */
public class BuilderAmendFunctions {

    private static Map<Class, Long> nextId = new HashMap<>();

    public static <T> Consumer<T> id(Long id) {
        return t -> setId(id, t);
    }

    public static <T> BiConsumer<Integer, T> incrementingIds() {
        return incrementingIds(0);
    }

    public static <T> BiConsumer<Integer, T> uniqueIds() {
        return (i, t) -> {
            Class<?> clazz = t.getClass();
            Long id = nextId.get(clazz) != null ? nextId.get(clazz) : 1L;
            setId(id, t);
            nextId.put(clazz, id + 1);
        };
    }

    public static <T> Consumer<T> idBasedNames(String prefix) {
        return names(t -> prefix + getId(t));
    }

    public static <T> BiConsumer<Integer, T> incrementingIds(int fromInclusive) {
        return (i, t) -> setId((long) i + fromInclusive, t);
    }

    public static <T> BiConsumer<Integer, T> names(BiFunction<Integer, T, String> nameGenerationFunction) {
        return (i, t) -> setName(nameGenerationFunction.apply(i, t), t);
    }

    public static <T> Consumer<T> names(Function<T, String> nameGenerationFunction) {
        return t -> setName(nameGenerationFunction.apply(t), t);
    }

    public static <T> Long getId(T instance) {
        return (Long) getField(instance, "id");
    }

    public static <T> T setId(Long value, T instance) {
        return setField("id", value, instance);
    }

    public static <T> T setName(Object value, T instance) {
        return setField("name", value, instance);
    }

    public static <T> T setField(String fieldName, Object value, T instance) {
        try {
            ReflectionTestUtils.invokeSetterMethod(instance, fieldName, value);
        } catch (Exception e) {
            ReflectionTestUtils.setField(instance, fieldName, value);
        }
        return instance;
    }
}
