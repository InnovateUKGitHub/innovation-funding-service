package com.worth.ifs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static java.util.stream.Collectors.groupingBy;

/**
 * Helper utilities related to Maps
 */
public class MapFunctions {

    /**
     * Given an even number of name value pairs as vararg arguments e.g. asMap("key1", 1L, "key2", 2L), this function will
     * return a Map with the given keys and their respective values
     *
     * @param nameValuePairs
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Map<T, R> asMap(Object... nameValuePairs) {

        if (nameValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Should have an even number of names and values in list");
        }

        Map<T, R> map = new HashMap<>();

        for (int i = 0; i < nameValuePairs.length; i += 2) {
            T key = (T) nameValuePairs[i];
            R value = (R) nameValuePairs[i + 1];
            map.put(key, value);
        }

        return map;
    }

    /**
     * Given a list and a function to apply to each of the items in order to extract a group from them, return a descending ordered
     * count of the groupings
     *
     * @param list
     * @param groupFn
     * @param <T>
     * @param <R>
     * @return
     */
    // TODO DW - INFUND-854 - add unit test
    public static <T, R> List<Map.Entry<R, Integer>> getSortedGroupingCounts(List<T> list, Function<T, R> groupFn) {

        Map<R, List<T>> grouped = list.stream().collect(groupingBy(groupFn));
        Map<R, Integer> numberOfOccurrancesByGrouping =
                simpleToMap(new ArrayList<>(grouped.entrySet()), Map.Entry::getKey, entry -> entry.getValue().size());

        List<Map.Entry<R, Integer>> entries = new ArrayList<>(numberOfOccurrancesByGrouping.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        return entries;
    }
}
