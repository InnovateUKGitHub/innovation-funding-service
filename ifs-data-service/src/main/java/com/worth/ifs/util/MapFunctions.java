package com.worth.ifs.util;

import java.util.HashMap;
import java.util.Map;

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
}
