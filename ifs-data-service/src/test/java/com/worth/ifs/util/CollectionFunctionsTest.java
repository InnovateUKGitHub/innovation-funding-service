package com.worth.ifs.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static org.junit.Assert.*;

/**
 *
 */
public class CollectionFunctionsTest {

    @Test
    public void test_flattenLists() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), asList(4, 5), singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 4, 5, 6);

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_nullSafe() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), null, singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_emptyElements() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), emptyList(), singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_allEmpty() {

        List<List<Integer>> deepList = asList(emptyList(), emptyList(), emptyList());
        List<Integer> expectedFlatList = emptyList();

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void test_combineLists() {

        List<Integer> list1 = asList(1, 2, 3);
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);

        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineLists_nullSafe() {

        List<Integer> list1 = null;
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(4, 5, 6);

        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineLists_emptyElements() {

        List<Integer> list1 = emptyList();
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(4, 5, 6);

        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineListsWithListAndVarargs() {

        List<Integer> list = asList(1, 2, 3);
        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list, 4, 5, 6));
    }

    @Test
    public void test_combineListsWithListAndVarargsNullSafe() {
        List<Integer> expectedCombinedList = asList(4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(null, 4, 5, 6));
    }

    @Test
    public void test_combineListsWithElementAndVarargs() {
        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void test_combineListsWithElementAndVarargsEmptyVarargs() {
        List<Integer> expectedCombinedList = asList(1);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(1));
    }

    @Test
    public void test_combineListsWithElementAndVarargsNullElementSafe() {
        List<Integer> expectedCombinedList = asList(2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(null, 2, 3, 4, 5, 6));
    }

    @Test
    public void test_forEachWithIndex() {

        final List<Integer> indicesSeen = new ArrayList<>();
        final List<String> valuesSeen = new ArrayList<>();
        final List<String> valuesToIterateOver = asList("string 1", "string 2", "string 3");

        CollectionFunctions.forEachWithIndex(valuesToIterateOver, (i, value) -> {
            indicesSeen.add(i);
            valuesSeen.add(value);
        });

        assertEquals(asList(0, 1, 2), indicesSeen);
        assertEquals(asList("string 1", "string 2", "string 3"), valuesSeen);
    }

    @Test
    public void test_forEachWithIndex_nullSafe() {

        final List<Integer> indicesSeen = new ArrayList<>();
        final List<String> valuesSeen = new ArrayList<>();
        final List<String> valuesToIterateOver = asList("string 1", null, "string 3");

        CollectionFunctions.forEachWithIndex(valuesToIterateOver, (i, value) -> {
            indicesSeen.add(i);
            valuesSeen.add(value);
        });

        assertEquals(asList(0, 1, 2), indicesSeen);
        assertEquals(asList("string 1", null, "string 3"), valuesSeen);
    }

    @Test
    public void test_forEachWithIndex_emptyValues() {

        final List<Integer> indicesSeen = new ArrayList<>();
        final List<String> valuesSeen = new ArrayList<>();
        final List<String> valuesToIterateOver = emptyList();

        CollectionFunctions.forEachWithIndex(valuesToIterateOver, (i, value) -> {
            indicesSeen.add(i);
            valuesSeen.add(value);
        });

        assertEquals(emptyList(), indicesSeen);
        assertEquals(emptyList(), valuesSeen);
    }

    @Test
    public void test_mapWithIndex() {

        final List<Integer> indicesSeen = new ArrayList<>();
        final List<String> valuesToIterateOver = asList("string 1", "string 2", "string 3");

        List<String> newStrings = CollectionFunctions.mapWithIndex(valuesToIterateOver, (i, value) -> {
            indicesSeen.add(i);
            return value + " changed";
        });

        assertEquals(asList(0, 1, 2), indicesSeen);
        assertEquals(asList("string 1 changed", "string 2 changed", "string 3 changed"), newStrings);
    }


    @Test
    public void test_mapWithIndex_nullSafe() {

        final List<Integer> indicesSeen = new ArrayList<>();
        final List<String> valuesToIterateOver = asList("string 1", null, "string 3");

        List<String> newStrings = CollectionFunctions.mapWithIndex(valuesToIterateOver, (i, value) -> {
            indicesSeen.add(i);
            return value != null ? value + " changed" : null;
        });

        assertEquals(asList(0, 1, 2), indicesSeen);
        assertEquals(asList("string 1 changed", null, "string 3 changed"), newStrings);
    }


    @Test
    public void test_mapWithIndex_emptyElements() {

        final List<Integer> indicesSeen = new ArrayList<>();
        final List<String> valuesToIterateOver = emptyList();

        List<String> newStrings = CollectionFunctions.mapWithIndex(valuesToIterateOver, (i, value) -> {
            indicesSeen.add(i);
            return value + " changed";
        });

        assertEquals(emptyList(), indicesSeen);
        assertEquals(emptyList(), newStrings);
    }

    @Test
    public void test_reverse() {

        List<String> originalList = asList("string 1", "another string 2", "string 3");
        List<String> reversed = CollectionFunctions.reverse(originalList);
        assertEquals(asList("string 3", "another string 2", "string 1"), reversed);
    }

    @Test
    public void test_reverse_nullElements() {

        List<String> originalList = asList(null, null, "string 3");
        List<String> reversed = CollectionFunctions.reverse(originalList);
        assertEquals(asList("string 3", null, null), reversed);
    }

    @Test
    public void test_reverse_nullSafe() {
        assertEquals(new ArrayList<>(), CollectionFunctions.reverse(null));
    }

    @Test
    public void test_reverse_empty() {
        assertEquals(new ArrayList<>(), CollectionFunctions.reverse(new ArrayList<>()));
    }

    @Test
    public void test_getOnlyElement() {
        assertEquals("hi there", CollectionFunctions.getOnlyElement(singletonList("hi there")));
    }

    @Test
    public void test_nullElement() {
        List<Object> nullElementList = new ArrayList<>();
        nullElementList.add(null);
        assertNull(CollectionFunctions.getOnlyElement(nullElementList));
    }

    @Test
    public void test_getOnlyElement_tooManyElements() {
        try {
            CollectionFunctions.getOnlyElement(asList("hi there", "goodbye!"));
            fail("Should have thrown an IllegalArgumentException as there were too many elements");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_getOnlyElement_notEnoughElements() {
        try {
            CollectionFunctions.getOnlyElement(emptyList());
            fail("Should have thrown an IllegalArgumentException as there weren't enough elements");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_getOnlyElement_notEnoughElements_nullList() {
        try {
            CollectionFunctions.getOnlyElement(null);
            fail("Should have thrown an IllegalArgumentException as there weren't enough elements (null list)");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_simpleMap() {
        assertEquals(asList("123 string", "456 string"), CollectionFunctions.simpleMap(asList(123, 456), i -> i + " string"));
    }

    @Test
    public void test_simpleMap_nullList() {
        assertEquals(asList(), CollectionFunctions.simpleMap(null, i -> i + " string"));
    }

    @Test
    public void test_simpleMap_nullElements() {
        assertEquals(asList("123 string", "null string"), CollectionFunctions.simpleMap(asList(123, null), i -> i + " string"));
    }

    @Test
    public void test_simpleFilter() {
        assertEquals(asList(789), CollectionFunctions.simpleFilter(asList(123, 456, 789), i -> i > 456));
    }

    @Test
    public void test_simpleFilter_nullList() {
        assertEquals(asList(), CollectionFunctions.simpleFilter(null, i -> false));
    }

    @Test
    public void test_simpleFilter_nullElements() {
        assertEquals(asList(789), CollectionFunctions.simpleFilter(asList(123, null, 456, 789), i -> i != null && i > 456));
    }

    @Test
    public void test_simpleFilterNot() {
        assertEquals(asList(123, 456), CollectionFunctions.simpleFilterNot(asList(123, 456, 789), i -> i > 456));
    }

    @Test
    public void test_simpleFilterNot_nullList() {
        assertEquals(asList(), CollectionFunctions.simpleFilterNot(null, i -> false));
    }

    @Test
    public void test_simpleFilterNot_nullElements() {
        assertEquals(asList(123, null, 456), CollectionFunctions.simpleFilterNot(asList(123, null, 456, 789), i -> i != null && i > 456));
    }

    @Test
    public void testToLinkedMap() {
        List<String> orderedList = Arrays.asList("1", "2", "3", "4", "5");
        Map<String, String> orderedMap = orderedList.stream().collect(CollectionFunctions.toLinkedMap(item -> item, item -> item + item));
        int index = 0;
        for (Entry<String, String> entry : orderedMap.entrySet()) {
            // Order is what we are testing
            assertEquals(orderedList.get(index), entry.getKey());
            assertEquals(orderedList.get(index) + orderedList.get(index), entry.getValue());
            index++;
        }
        assertEquals(orderedList.size(), index);
    }

    @Test
    public void testToLinkedMapDuplicateEntry() {
        List<String> orderedList = Arrays.asList("1", "2", "3", "4", "5", "4");
        try {
            orderedList.stream().collect(CollectionFunctions.toLinkedMap(item -> item, item -> item + item));
            fail("Should have failed with illegal state exception");
        } catch (IllegalStateException e) {
            // Expected  behaviour
        }
    }

    public void test_simpleJoiner() {
        assertEquals("123, 456, 789", CollectionFunctions.simpleJoiner(asList(123, 456, 789), ", "));
    }

    @Test
    public void test_simpleJoiner_nullList() {
        assertEquals("", CollectionFunctions.simpleJoiner(null, ", "));
    }

    @Test
    public void test_simpleJoiner_nullElements() {
        assertEquals("123, , 789", CollectionFunctions.simpleJoiner(asList(123, null, 789), ", "));
    }

    @Test
    public void testSimpleToMapWithKeyAndValueMappers() {

        Map<Integer, String> toMap = CollectionFunctions.simpleToMap(asList(1, 2, 3), element -> element + 10, element -> element + " value");
        assertEquals(3, toMap.size());
        assertTrue(toMap.keySet().contains(11));
        assertTrue(toMap.keySet().contains(12));
        assertTrue(toMap.keySet().contains(13));
        assertEquals("1 value", toMap.get(11));
        assertEquals("2 value", toMap.get(12));
        assertEquals("3 value", toMap.get(13));
    }

    @Test
    public void testSimpleToMapWithKeyAndValueMappersNullSafe() {

        Map<Integer, Integer> toMap = CollectionFunctions.simpleToMap(null, identity(), identity());
        assertTrue(toMap.isEmpty());
    }

    @Test
    public void testSimpleToMapWithKeyMapper() {

        Map<Integer, Integer> toMap = CollectionFunctions.simpleToMap(asList(1, 2, 3), element -> element + 10);
        assertEquals(3, toMap.size());
        assertTrue(toMap.keySet().contains(11));
        assertTrue(toMap.keySet().contains(12));
        assertTrue(toMap.keySet().contains(13));
        assertEquals(Integer.valueOf(1), toMap.get(11));
        assertEquals(Integer.valueOf(2), toMap.get(12));
        assertEquals(Integer.valueOf(3), toMap.get(13));
    }

    @Test
    public void testSimpleToMapWithKeyMapperNullSafe() {

        Map<Integer, Integer> toMap = CollectionFunctions.simpleToMap(null, identity());
        assertTrue(toMap.isEmpty());
    }
}

