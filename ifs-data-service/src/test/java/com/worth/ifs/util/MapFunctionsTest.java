package com.worth.ifs.util;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
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

    @Test
    public void testGetSortedGroupingCounts() {

        List<SortGroupTest> groupable = simpleMap(asList("string 1", "string 1", "string 2", "string 2", "string 2", "string 2", "string 3"), SortGroupTest::new);
        Map<String, Integer> groupedCountedAndSorted = MapFunctions.getSortedGroupingCounts(groupable, SortGroupTest::getValue);

        assertEquals(3, groupedCountedAndSorted.size());

        assertEquals(Integer.valueOf(4), groupedCountedAndSorted.get("string 2"));
        assertEquals(Integer.valueOf(2), groupedCountedAndSorted.get("string 1"));
        assertEquals(Integer.valueOf(1), groupedCountedAndSorted.get("string 3"));
    }

    @Test
    public void testGetSortedGroupingCountsNullSafe() {

        Map<String, Integer> groupedCountedAndSorted = MapFunctions.getSortedGroupingCounts((List<SortGroupTest>) null, SortGroupTest::getValue);
        assertEquals(0, groupedCountedAndSorted.size());
    }

    private class SortGroupTest {

        private String value;

        private SortGroupTest(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
