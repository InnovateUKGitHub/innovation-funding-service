package com.worth.ifs.util;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests around helper utilities related to Maps
 */
public class MapFunctionsTest {

    @Test
    public void testAsMap() {

        Map<Long, String> map = MapFunctions.asMap(1L, "String 1", 2L, "String 2");

        assertEquals(2, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsKey(2L));
        assertEquals("String 1", map.get(1L));
        assertEquals("String 2", map.get(2L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAsMapWithUnevenNamesAndValues() {
        MapFunctions.asMap(1L, "String 1", 2L);
    }

    @Test
    public void testAsMapEmptyNameValuePairs() {

        Map<Long, String> map = MapFunctions.asMap();
        assertTrue(map.isEmpty());
    }
}
