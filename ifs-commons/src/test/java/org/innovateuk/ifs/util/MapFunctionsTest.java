package org.innovateuk.ifs.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests around helper utilities related to Maps
 */
public class MapFunctionsTest {

    @Test
    public void testAsMap() {

        Map<Long, String> map = asMap(1L, "String 1", 2L, "String 2");

        assertEquals(2, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsKey(2L));
        assertEquals("String 1", map.get(1L));
        assertEquals("String 2", map.get(2L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAsMapWithUnevenNamesAndValues() {
        asMap(1L, "String 1", 2L);
    }

    @Test
    public void testAsMapEmptyNameValuePairs() {

        Map<Long, String> map = asMap();
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

        Map<String, Integer> groupedCountedAndSorted = MapFunctions.getSortedGroupingCounts(null, SortGroupTest::getValue);
        assertEquals(0, groupedCountedAndSorted.size());
    }

    @Test
    public void testCombineMaps() {

        Map<Long, String> map1 = asMap(1L, "1", 2L, "2");
        Map<Long, String> map2 = asMap(3L, "3");

        assertEquals(asMap(1L, "1", 2L, "2", 3L, "3"), combineMaps(map1, map2));
    }

    @Test
    public void testCombineMapsWithDuplicateKeys() {

        Map<Long, String> map1 = asMap(1L, "1", 2L, "2");
        Map<Long, String> map2 = asMap(2L, "overridden", 3L, "3");

        assertEquals(asMap(1L, "1", 2L, "overridden", 3L, "3"), combineMaps(map1, map2));
    }

    @Test
    public void testCombineMapsNullSafe() {
        assertEquals(emptyMap(), combineMaps(null, null));
        assertEquals(asMap(1L, "1"), combineMaps(asMap(1L, "1"), null));
        assertEquals(asMap(1L, "1"), combineMaps(null, asMap(1L, "1")));
    }

    @Test
    public void testSimplePartition() {
        Map<Long, String> map1 = asMap(1L, "1", 2L, "2", 3L, "3");

        Pair<Map<Long, String>, Map<Long, String>> result = simplePartition(map1, longStringEntry -> longStringEntry.getKey().equals(2L));

        assertEquals(asMap(2L, "2"), result.getLeft());
        assertEquals(asMap(1L, "1", 3L, "3"), result.getRight());

        Pair<Map<Long, String>, Map<Long, String>> result2 = simplePartition(map1, longStringEntry -> longStringEntry.getValue().equals("3"));

        assertEquals(asMap(3L, "3"), result2.getLeft());
        assertEquals(asMap(1L, "1", 2L, "2"), result2.getRight());

        Pair<Map<Long, String>, Map<Long, String>> result3 = simplePartition(map1, longStringEntry -> longStringEntry.getValue().equals("4"));

        assertEquals(emptyMap(), result3.getLeft());
        assertEquals(asMap(1L, "1", 2L, "2", 3L, "3"), result3.getRight());
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
