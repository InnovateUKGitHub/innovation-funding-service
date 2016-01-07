package com.worth.ifs;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * A set of functions that can be used by Builders to set fields.
 */
public class BuilderAmendFunctions {

    private static Map<Class, Long> nextId = new HashMap<>();

    public static void clearUniqueIds() {
        nextId = new HashMap<>();
    }

    public static <T> Consumer<T> id(Long id) {
        return t -> setId(id, t);
    }

    public static <T> Consumer<T> user(User user) {
        return t -> setUser(user, t);
    }

    public static <T> Consumer<T> competition(Competition competition) {
        return t -> setCompetition(competition, t);
    }

    public static <T> Consumer<T> application(Application application) {
        return t -> setApplication(application, t);
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

    public static Optional<Competition> getCompetition(Object object) {
        return Optional.ofNullable((Competition) getField(object, "competition"));
    }

    public static <T> T setId(Long value, T instance) {
        return setField("id", value, instance);
    }

    public static <T> T setApplication(Application value, T instance) {
        return setField("application", value, instance);
    }

    public static <T> T setCompetition(Competition value, T instance) {
        return setField("competition", value, instance);
    }

    public static <T> T setName(String value, T instance) {
        return setField("name", value, instance);
    }

    public static <T> T setUser(User value, T instance) {
        return setField("user", value, instance);
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

    public static Function<Integer, Integer> zeroBasedIndexes() {
        return Function.identity();
    }

    public static Function<Integer, Integer> oneBasedIndexes() {
        return index -> index + 1;
    }
}
