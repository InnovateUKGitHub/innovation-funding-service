package com.worth.ifs.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static java.util.stream.Collectors.groupingBy;

/**
 * Helper utilities related to Maps
 */
public final class MapFunctions {

	private MapFunctions() {}
	
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
    public static <T, R> LinkedHashMap<R, Integer> getSortedGroupingCounts(List<T> list, Function<T, R> groupFn) {

        if (list == null || list.isEmpty()) {
            return new LinkedHashMap<>(0);
        }

        Map<R, List<T>> grouped = list.stream().collect(groupingBy(groupFn));
        Map<R, Integer> numberOfOccurrancesByGrouping =
                simpleToMap(new ArrayList<>(grouped.entrySet()), Map.Entry::getKey, entry -> entry.getValue().size());

        List<Map.Entry<R, Integer>> entries = new ArrayList<>(numberOfOccurrancesByGrouping.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        LinkedHashMap<R, Integer> sortedMap = new LinkedHashMap<>();
        entries.forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
        return sortedMap;
    }

    /**
     * Converts the given map's entrySet to a list of Pairs
     *
     * @param map
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<Pair<T, R>> toListOfPairs(Map<T, R> map) {
        return simpleMap(map.entrySet(), entry -> Pair.of(entry.getKey(), entry.getValue()));
    }

    /**
     * Given 2 maps, this method will return a non-null Map containing all the elements of both.  If however map2 contains duplicate
     * keys with map1, the returned Map will contain map2's versions of these
     *
     * @param map1
     * @param map2
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Map<T, R> combineMaps(Map<T, R> map1, Map<T, R> map2) {

        if (map1 == null && map2 == null) {
            return new HashMap<>(0);
        }

        if (map1 == null) {
            return new HashMap<>(map2);
        }

        if (map2 == null) {
            return new HashMap<>(map1);
        }

        Map<T, R> combined = new HashMap<>(map1);
        map2.forEach((key, value) -> combined.put(key, value));
        return combined;
    }
}
