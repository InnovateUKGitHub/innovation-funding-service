package com.worth.ifs.util;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Utility class to provide useful reusable Functions around Collections throughout the codebase
 */
public class CollectionFunctions {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(CollectionFunctions.class);

    /**
     * Flatten the given 2-dimensional List into a 1-dimensional List
     *
     * @param lists
     * @param <T>
     * @return 1-dimensional list
     */
    public static <T> List<T> flattenLists(List<List<T>> lists) {
        return lists.stream()
                .filter(l -> l != null)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    /**
     * Combine the given Lists into a single List
     *
     * @param lists
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    @SafeVarargs
    public static <T> List<T> combineLists(List<T>... lists) {
        return doCombineLists(lists);
    }

    private static <T> List<T> doCombineLists(List<T>... lists) {
        List<T> combinedList = new ArrayList<>();

        for (List<T> list : lists) {
            if (list != null && !list.isEmpty()) {
                combinedList.addAll(list);
            }
        }

        return combinedList;
    }

    /**
     * Combine the given List and varargs array into a single List
     *
     * @param list
     * @param otherElements
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    @SafeVarargs
    public static <T> List<T> combineLists(List<T> list, T... otherElements) {
        List<T> startingList = new ArrayList<>();

        if (list != null && !list.isEmpty()) {
            startingList.addAll(list);
        }

        return doCombineLists(startingList, asList(otherElements));
    }

    /**
     * Combine the given element and varargs array into a single List
     *
     * @param firstElement
     * @param otherElements
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    @SafeVarargs
    public static <T> List<T> combineLists(T firstElement, T... otherElements) {
        return doCombineLists(asList(firstElement), asList(otherElements));
    }


    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well
     *
     * @param consumer
     * @param <T>
     * @return a Consumer that can then be applied to Lists to apply the consumer function provided
     */
    public static <T> Consumer<List<T>> forEachConsumer(BiConsumer<Integer, T> consumer) {
        return list -> IntStream.range(0, list.size()).forEach(i -> consumer.accept(i, list.get(i)));
    }

    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well
     *
     * @param consumer
     * @param <T>
     */
    public static <T> void forEachWithIndex(List<T> list, BiConsumer<Integer, T> consumer) {
        forEachConsumer(consumer).accept(list);
    }

    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well and the ability to return values
     *
     * @param function
     * @param <T>
     * @return a Function that can then be applied to Lists to apply the function provided in order to produce a result
     */
    public static <T, R> Function<List<T>, List<R>> forEachFunction(BiFunction<Integer, T, R> function) {
        return list -> IntStream.range(0, list.size()).mapToObj(i -> function.apply(i, list.get(i))).collect(toList());
    }

    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well and the ability to return values
     *
     * @param function
     * @param <T>
     * @return a Function that can then be applied to Lists to apply the function provided in order to produce a result
     */
    public static <T, R> List<R> mapWithIndex(List<T> list, BiFunction<Integer, T, R> function) {
        return forEachFunction(function).apply(list);
    }

    /**
     * Returns a new List with the original list's contents reversed.  Leaves the original list intact.  Returns
     * an empty list if a null or empty list is supplied.
     *
     * @param original
     * @param <T>
     * @return
     */
    public static <T> List<T> reverse(List<T> original) {
        List<T> reversed = original != null ? new ArrayList<>(original) : new ArrayList<>();
        Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Return the one and only element in the given list, otherwise throw an IllegalArgumentException
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T getOnlyElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("No elements were available in list " + list + ", so cannot return only element");
        }

        if (list.size() > 1) {
            throw new IllegalArgumentException("More than one element was available in list " + list + ", so cannot return only element");
        }

        return list.get(0);
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     *
     * @param list
     * @param mappingFn
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> simpleMap(List<T> list, Function<T, R> mappingFn) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(mappingFn).collect(toList());
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     *
     * @param set
     * @param mappingFn
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> simpleMap(Set<T> set, Function<T, R> mappingFn) {
        if (set == null || set.isEmpty()) {
            return Collections.emptyList();
        }
        return set.stream().map(mappingFn).collect(toList());
    }

    /**
     * A map collector that preserves order
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <U>
     * @return
     */
    public static <T, K, U> Collector<T, ?, Map<K,U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper, valueMapper, (u, v) -> {throw new IllegalStateException(String.format("Duplicate key %s", u));}, LinkedHashMap::new);
    }

    /**
     * A simple function to convert a list of items to a Map given a key generating function and a value generating function
     *
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <U>
     * @return
     */
    public static <T, K, U> Map<K,U> simpleToMap(
            List<T> list,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {

        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }

        return list.stream().collect(toMap(keyMapper, valueMapper));
    }

    /**
     * A simple function to convert a list of items to a Map of keys against the original elements themselves, given a key generating function
     *
     * @param keyMapper
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> Map<K,T> simpleToMap(
            List<T> list,
            Function<? super T, ? extends K> keyMapper) {

        return simpleToMap(list, keyMapper, identity());
    }

    /**
     * A collector that maps a collection of pairs into a map.
     * @param <R>
     * @param <T>
     * @return
     */
    public static <R, T> Collector<Pair<R, T>, ?, Map<R, T>> pairsToMap() {
        return toMap(Pair::getLeft, Pair::getRight);
    }

    /**
     * Function that takes a map entry and returns the value. Useful for collecting over collections of entry sets
     * @param <R>
     * @param <T>
     * @return
     */
    public static <R, T> Function<Map.Entry<R, T>, T> mapEntryValue() {
        return Map.Entry::getValue;
    }

    /**
     * A simple wrapper around a 1-stage filter function, to remove boilerplate from production code
     *
     * @param list
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> List<T> simpleFilter(List<T> list, Predicate<T> filterFn) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().filter(filterFn).collect(toList());
    }

    /**
     * A simple wrapper around a NEGATED 1-stage filter function, to remove boilerplate from production code
     *
     * @param list
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> List<T> simpleFilterNot(List<T> list, Predicate<T> filterFn) {
        return simpleFilter(list, element -> !filterFn.test(element));
    }

    /**
     * A simple wrapper around a String joining function.  Returns a string of the given list, separated by the given
     * joinString
     *
     * @param list
     * @param joinString
     * @param <T>
     * @return
     */
    public static <T> String simpleJoiner(List<T> list, String joinString) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream().map(element -> element != null ? element.toString() : "").collect(joining(joinString));
    }
}
