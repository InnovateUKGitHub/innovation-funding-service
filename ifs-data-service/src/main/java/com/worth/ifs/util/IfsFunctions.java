package com.worth.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Utility class to provide useful reusable Functions throughout the codebase
 */
public class IfsFunctions {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(IfsFunctions.class);

    /**
     * Flatten the given 2-dimensional List into a 1-dimensional List
     *
     * @param lists
     * @param <T>
     * @return 1-dimensional list
     */
    public static <T> List<T> flattenLists(List<List<T>> lists) {
        return lists.stream()
                .flatMap(l -> l.stream())
                .collect(toList());
    }

    /**
     * Combine the given Lists into a single List
     *
     * @param lists
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    public static <T> List<T> combineLists(List<T>... lists) {

        List<T> combinedList = new ArrayList<>();

        for (List<T> list : lists) {
            combinedList.addAll(list);
        }

        return combinedList;
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
    public static <T> void forEach(List<T> list, BiConsumer<Integer, T> consumer) {
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
    public static <T, R> List<R> forEachProduce(List<T> list, BiFunction<Integer, T, R> function) {
        return forEachFunction(function).apply(list);
    }

}
