package org.innovateuk.ifs.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.function.Function.identity;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.CollectionFunctions.sort;
import static org.junit.Assert.*;

public class CollectionFunctionsTest {

    @Test
    public void test_simpleGroupBy(){
        Map<String, String> map = new HashMap<>();
        map.put("formInput[1].MMYYYY.year", "2001");
        map.put("formInput[1].MMYYYY.month", "11");
        map.put("formInput[2].MMYYYY.year", "1968");
        map.put("formInput[2].MMYYYY.month", "2");
        map.put("formInput[3].MMYYYY.year", "2001");
        map.put("formInput[3].MMYYYY.month", "11");

        // Method under test.
        Map<String, Map<String, String>> simpleGroupBy = simpleGroupBy(map, key -> key.replace(".MMYYYY.month", "").replace(".MMYYYY.year", ""));
        assertEquals(3, simpleGroupBy.size());
        // formInput[1]
        assertNotNull(simpleGroupBy.get("formInput[1]"));
        assertEquals("2001", simpleGroupBy.get("formInput[1]").get("formInput[1].MMYYYY.year"));
        assertEquals("11", simpleGroupBy.get("formInput[1]").get("formInput[1].MMYYYY.month"));

        // formInput[2]
        assertNotNull(simpleGroupBy.get("formInput[2]"));
        assertEquals("1968", simpleGroupBy.get("formInput[2]").get("formInput[2].MMYYYY.year"));
        assertEquals("2", simpleGroupBy.get("formInput[2]").get("formInput[2].MMYYYY.month"));

        // formInput[3]
        assertNotNull(simpleGroupBy.get("formInput[3]"));
        assertEquals("2001", simpleGroupBy.get("formInput[3]").get("formInput[3].MMYYYY.year"));
        assertEquals("11", simpleGroupBy.get("formInput[3]").get("formInput[3].MMYYYY.month"));
    }

    @Test
    public void test_simpleGroupByNull(){
        Map<String, String> map = new HashMap<>();
        map.put("formInput[1].MMYYYY.year", null);
        map.put("formInput[1].MMYYYY.month", null);

        // Method under test.
        Map<String, Map<String, String>> simpleGroupBy = simpleGroupBy(map, key -> key.replace(".MMYYYY.month", "").replace(".MMYYYY.year", ""));
        assertEquals(1, simpleGroupBy.size());
        // formInput[1]
        assertNotNull(simpleGroupBy.get("formInput[1]"));
        assertNull(simpleGroupBy.get("formInput[1]").get("formInput[1].MMYYYY.year"));
        assertNull(simpleGroupBy.get("formInput[1]").get("formInput[1].MMYYYY.month"));
    }

    @Test
    public void test_flattenLists() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), asList(4, 5), singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 4, 5, 6);

        assertEquals(expectedFlatList, flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_nullSafe() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), null, singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);

        assertEquals(expectedFlatList, flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_emptyElements() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), emptyList(), singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);

        assertEquals(expectedFlatList, flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_allEmpty() {

        List<List<Integer>> deepList = asList(emptyList(), emptyList(), emptyList());
        List<Integer> expectedFlatList = emptyList();

        assertEquals(expectedFlatList, flattenLists(deepList));
    }

    @Test
    public void test_flattenSets() {

        Set<Set<Integer>> deepList = asLinkedSet(asLinkedSet(1, 2, 3), asLinkedSet(4, 5), singleton(6));
        Set<Integer> expectedFlatList = asLinkedSet(1, 2, 3, 4, 5, 6);

        assertEquals(expectedFlatList, flattenSets(deepList));
    }

    @Test
    public void test_combineLists() {

        List<Integer> list1 = asList(1, 2, 3);
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);

        assertEquals(expectedCombinedList, combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineLists_nullSafe() {

        List<Integer> list1 = null;
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(4, 5, 6);

        assertEquals(expectedCombinedList, combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineLists_emptyElements() {

        List<Integer> list1 = emptyList();
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(4, 5, 6);

        assertEquals(expectedCombinedList, combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineListsWithListAndVarargs() {

        List<Integer> list = asList(1, 2, 3);
        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, combineLists(list, 4, 5, 6));
    }

    @Test
    public void test_combineListsWithListAndVarargsNullSafe() {
        List<Integer> expectedCombinedList = asList(4, 5, 6);
        assertEquals(expectedCombinedList, combineLists(null, 4, 5, 6));
    }

    @Test
    public void test_combineListsWithElementAndVarargs() {
        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, combineLists(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void test_combineListsWithElementAndVarargsEmptyVarargs() {
        List<Integer> expectedCombinedList = singletonList(1);
        assertEquals(expectedCombinedList, combineLists(1));
    }

    @Test
    public void test_combineListsWithElementAndVarargsNullElementSafe() {
        List<Integer> expectedCombinedList = asList(2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, combineLists(null, 2, 3, 4, 5, 6));
    }

    @Test
    public void test_combineListsWithElementAndList() {

        List<Integer> list = asList(1, 2, 3);

        List<Integer> expectedCombinedList = asList(0, 1, 2, 3);
        assertEquals(expectedCombinedList, combineLists(0, list));
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
        assertEquals(asList("123 string", "456 string"), simpleMap(asList(123, 456), i -> i + " string"));
    }

    @Test
    public void test_simpleMap_nullList() {
        assertEquals(emptyList(), simpleMap((List<?>) null, i -> i + " string"));
    }

    @Test
    public void test_simpleMap_nullElements() {
        assertEquals(asList("123 string", "null string"), simpleMap(asList(123, null), i -> i + " string"));
    }

    @Test
    public void test_simpleMap_withArray() {
        assertEquals(asList("123 string", "456 string"), simpleMap(new Integer[] {123, 456}, i -> i + " string"));
    }

    @Test
    public void test_simpleMap_withMap() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        assertEquals(asList("1a", "2b"), simpleMap(map, (key, value) -> key + value));
    }

    @Test
    public void test_simpleMapKey() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        Map<Integer, String> expected = new HashMap<>();
        expected.put(2, "a");
        expected.put(4, "b");

        assertEquals(expected, simpleMapKey(map, key -> key * 2));
    }

    @Test
    public void test_simpleMapValue() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        Map<Integer, String> expected = new HashMap<>();
        expected.put(1, "a!");
        expected.put(2, "b!");

        assertEquals(expected, simpleMapValue(map, value -> value  + "!"));
    }

    @Test
    public void test_simpleMapEntry() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        Map<String, String> expected = new HashMap<>();
        expected.put("2a!", "4a?");
        expected.put("4b!", "8b?");

        assertEquals(expected, simpleMapEntry(map,
                entry -> entry.getKey() * 2 + entry.getValue() + "!",
                entry -> entry.getKey() * 4 + entry.getValue() + "?"));
    }

    @Test
    public void test_simpleMapWithIndex() {
        assertEquals(asList("123 string 0", "456 string 1"),
                simpleMapWithIndex(asList(123, 456), (element, index) -> element + " string " + index));
    }

    @Test
    public void test_simpleMapWithIndex_emptyList() {
        assertEquals(emptyList(),
                simpleMapWithIndex(emptyList(), (element, index) -> element + " string " + index));
    }

    @Test
    public void test_simpleMapWithIndex_withArray() {
        assertEquals(asList("123 string 0", "456 string 1"),
                simpleMapWithIndex(new Integer[] {123, 456}, (element, index) -> element + " string " + index));
    }

    @Test
    public void test_simpleMapArray() {
        assertEquals(new String[] {"123 string", "456 string"},
                simpleMapArray(new Integer[] {123, 456}, element -> element + " string", String.class));
    }

    @Test
    public void test_simpleMapArray_nullArray() {
        assertEquals(new String[] {},
                simpleMapArray(null, element -> element + " string ", String.class));
    }

    @Test
    public void test_simpleMapArray_emptyArray() {
        assertEquals(new String[] {},
                simpleMapArray(new String[] {}, element -> element + " string ", String.class));
    }

    @Test
    public void test_simpleMapSet() {
        assertEquals(asLinkedSet("123 string", "456 string"), simpleMapSet(asLinkedSet(123, 456), i -> i + " string"));
    }

    @Test
    public void test_simpleMapSet_null() {
        assertEquals(emptySet(), simpleMapSet((Set<?>) null, i -> i + " string"));
    }

    @Test
    public void test_simpleMapSet_withList() {
        assertEquals(asLinkedSet("123 string", "456 string"), simpleMapSet(asList(123, 456), i -> i + " string"));
    }

    @Test
    public void test_simpleMapSet_withEmptyList() {
        assertEquals(emptySet(), simpleMapSet(emptyList(), i -> i + " string"));
    }

    @Test
    public void test_simpleMapSet_withArray() {
        assertEquals(asLinkedSet("123 string", "456 string"), simpleMapSet(new Integer[] {123, 456}, i -> i + " string"));
    }

    @Test
    public void test_simpleMapSet_withEmptyArray() {
        assertEquals(emptySet(), simpleMapSet(new Integer[] {}, i -> i + " string"));
    }

    @Test
    public void test_simpleToLinkedHashSetWithCollection() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "456 string")),
                CollectionFunctions.simpleToLinkedHashSet(asList(123, 456), i -> i + " string"));
    }

    @Test
    public void test_simpleToLinkedHashSetWithCollection_nullList() {
        assertEquals(new LinkedHashSet<String>(),
                CollectionFunctions.simpleToLinkedHashSet((List<?>) null, i -> i + " string"));
    }

    @Test
    public void test_arrayToLinkedHashSetWithCollection_nullElements() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "null string")),
                CollectionFunctions.simpleToLinkedHashSet(asList(123, null), i -> i + " string"));
    }

    @Test
    public void test_toLinkedHashSetWithArray() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "456 string")),
                CollectionFunctions.simpleToLinkedHashSet(new Integer[]{123, 456}, i -> i + " string"));
    }

    @Test
    public void test_arrayToLinkedHashSetWithArray_nullArray() {
        String[] stringArray = null;

        assertEquals(new LinkedHashSet<String>(),
                CollectionFunctions.simpleToLinkedHashSet(stringArray, i -> i + " string"));
    }

    @Test
    public void test_simpleToLinkedHashSetWithArray_nullElements() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "null string")),
                CollectionFunctions.simpleToLinkedHashSet(new Integer[]{123, null}, i -> i + " string"));
    }

    @Test
    public void test_simpleFilter() {
        assertEquals(singletonList(789), simpleFilter(asList(123, 456, 789), i -> i > 456));
    }

    @Test
    public void test_simpleFilter_nullList() {
        assertEquals(emptyList(), simpleFilter((Collection<?>)null, i -> false));
    }

    @Test
    public void test_simpleFilter_nullElements() {
        assertEquals(singletonList(789), simpleFilter(asList(123, null, 456, 789), i -> i != null && i > 456));
    }

    @Test
    public void test_simpleFilterNot() {
        assertEquals(asList(123, 456), simpleFilterNot(asList(123, 456, 789), i -> i > 456));
    }

    @Test
    public void test_simpleFilterNot_nullList() {
        assertEquals(emptyList(), simpleFilterNot((List<?>)null, i -> false));
    }

    @Test
    public void test_simpleFilterNot_nullElements() {
        assertEquals(asList(123, null, 456), simpleFilterNot(asList(123, null, 456, 789), i -> i != null && i > 456));
    }

    @Test
    public void test_simpleFilter_withArray() {
        assertEquals(singletonList(789), simpleFilter(new Integer[] {123, 456, 789}, i -> i > 456));
    }

    @Test
    public void test_simpleFilter_withEmptyArray() {
        assertEquals(emptyList(), simpleFilter((Integer[]) null, i -> i > 456));
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

    @Test(expected = IllegalStateException.class)
    public void testToLinkedMapDuplicateEntry() {
        List<String> orderedList = Arrays.asList("1", "2", "3", "4", "5", "4");
        orderedList.stream().collect(CollectionFunctions.toLinkedMap(item -> item, item -> item + item));
    }

    @Test
    public void test_simpleJoiner() {
        assertEquals("123, 456, 789", simpleJoiner(asList(123, 456, 789), ", "));
    }

    @Test
    public void test_simpleJoiner_nullList() {
        assertEquals("", simpleJoiner(null, ", "));
    }

    @Test
    public void test_simpleJoiner_nullElements() {
        assertEquals("123, , 789", simpleJoiner(asList(123, null, 789), ", "));
    }

    @Test
    public void test_simpleJoiner_withTransformer() {
        assertEquals("123!, 456!, 789!", simpleJoiner(asList(123, 456, 789), i -> i + "!", ", "));
    }

    @Test
    public void test_simpleJoiner_withTransformer_nullList() {
        assertEquals("", simpleJoiner(null, i -> i + "!", ", "));
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

    @Test
    public void simpleToLinkedMapWhenInputListIsNull() {

        Map<Integer, Integer> toLinkedMap = simpleToLinkedMap(null, identity(), identity());
        assertTrue(toLinkedMap.isEmpty());
    }

    @Test
    public void simpleToLinkedMapWhenInputListIsEmpty() {

        Map<Integer, Integer> toLinkedMap = simpleToLinkedMap(Collections.EMPTY_LIST, identity(), identity());
        assertTrue(toLinkedMap.isEmpty());
    }

    @Test
    public void test_simpleToLinkedMap() {

        List<Integer> inputList = Arrays.asList(1, 2, 3, 4, 5, 6);
        Map<Integer, String> toLinkedMap = simpleToLinkedMap(inputList, element -> element + 10, element -> element + " value");

        // Assertion
        int index = 0;
        for (Entry<Integer, String> entry : toLinkedMap.entrySet()) {

            // Ensure that the elements are present in the correct order

            Integer expectedKey = inputList.get(index) + 10;
            String expectedValue = inputList.get(index) + " value";

            assertEquals(expectedKey, entry.getKey());
            assertEquals(expectedValue, entry.getValue());
            index++;
        }
    }

    @Test
    public void test_simpleLinkedMapKeyAndValue() {

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "Test1");
        inputMap.put(2, "Test2");
        inputMap.put(3, "Test3");

        Map<Integer, String> toLinkedMap = simpleLinkedMapKeyAndValue(inputMap, element -> element + 10, element -> element + " value");

        // Assertion
        int inputKey = 1;
        for (Entry<Integer, String> entry : toLinkedMap.entrySet()) {

            // Ensure that the elements are present in the correct order

            Integer expectedKey = inputKey + 10;
            String expectedValue = inputMap.get(inputKey) + " value";

            assertEquals(expectedKey, entry.getKey());
            assertEquals(expectedValue, entry.getValue());
            inputKey++;
        }
    }

    @Test
    public void test_simpleLinkedMapKey() {

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "Test1");
        inputMap.put(2, "Test2");
        inputMap.put(3, "Test3");

        Map<Integer, String> toLinkedMap = simpleLinkedMapKey(inputMap, element -> element + 10);

        // Assertion
        int inputKey = 1;
        for (Entry<Integer, String> entry : toLinkedMap.entrySet()) {

            // Ensure that the elements are present in the correct order

            Integer expectedKey = inputKey + 10;
            String expectedValue = inputMap.get(inputKey);

            assertEquals(expectedKey, entry.getKey());
            assertEquals(expectedValue, entry.getValue());
            inputKey++;
        }
    }

    @Test
    public void test_simpleLinkedMapValue() {

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "Test1");
        inputMap.put(2, "Test2");
        inputMap.put(3, "Test3");

        Map<Integer, String> toLinkedMap = simpleLinkedMapValue(inputMap, element -> element + " value");

        // Assertion
        int inputKey = 1;
        for (Entry<Integer, String> entry : toLinkedMap.entrySet()) {

            // Ensure that the elements are present in the correct order

            Integer expectedKey = inputKey;
            String expectedValue = inputMap.get(inputKey) + " value";

            assertEquals(expectedKey, entry.getKey());
            assertEquals(expectedValue, entry.getValue());
            inputKey++;
        }
    }

    @Test
    public void testFindPermutations() {

        List<List<String>> permutations = CollectionFunctions.findPermutations(asList("a", "b", "c"));
        assertEquals(3 * 2 * 1, permutations.size());

        List<List<String>> expectedPermutations = asList(
                asList("a", "b", "c"),
                asList("b", "a", "c"),
                asList("c", "a", "b"),
                asList("a", "c", "b"),
                asList("b", "c", "a"),
                asList("c", "b", "a"));
        expectedPermutations.forEach(expected -> assertTrue(permutations.contains(expected)));
    }

    @Test
    public void test_removeElement() {
        List<String> tokens = asList("a", "b", "c");
        assertEquals(asList("a", "c"), removeElement(tokens, 1));
        assertEquals(asList("a", "b", "c"), tokens);
    }

    @Test
    public void test_removeDuplicates() {
        List<String> tokens = asList("a", "b", "c", "b", "e", "c");
        assertEquals(asList("a", "b", "c", "e"), removeDuplicates(tokens));
        assertEquals(asList("a", "b", "c", "b", "e", "c"), tokens);
    }

    @Test
    public void test_removeDuplicates_nullList() {
        assertEquals(emptyList(), removeDuplicates(null));
    }

    @Test
    public void test_sort() {
        assertEquals(asList("a", "b", "c"), sort(asList("b", "c", "a")));
    }

    @Test
    public void test_sort_nullSafe() {
        assertEquals(emptyList(), sort(null));
    }

    @Test
    public void test_sortWithComparator() {
        assertEquals(asList("c", "b", "a"), sort(asList("b", "c", "a"), (s1, s2) -> s2.compareTo(s1)));
    }

    @Test
    public void test_sortWithComparator_nullSafe() {
        assertEquals(emptyList(), sort((List<String>) null, (s1, s2) -> s2.compareTo(s1)));
    }

    @Test
    public void testSimplePartition() {
        Pair<List<String>, List<String>> partitioned = simplePartition(asList("a1", "b1", "a2", "c1"), string -> string.startsWith("a"));
        assertEquals(asList("a1", "a2"), partitioned.getLeft());
        assertEquals(asList("b1", "c1"), partitioned.getRight());
    }

    @Test
    public void testSimplePartition_NullSafe() {
        Pair<List<String>, List<String>> partitioned = simplePartition(null, string -> string.startsWith("a"));
        assertEquals(emptyList(), partitioned.getLeft());
        assertEquals(emptyList(), partitioned.getRight());
    }

    @Test
    public void testContainsAllEqualsFunction(){
        List<Pair<Integer, String>> containingList = asList(of(1, "two"), of(2, "pair"));
        List<String> containedList = asList("two", "pair");
        List<String> anotherSmallerContainedList = asList("two");
        List<String> isNotContainedList = asList("not present");
        assertTrue(containsAll(containingList, containedList, (s,t) -> s.getRight() == t));
        assertTrue(containsAll(containedList,containingList, (s,t) ->  s == t.getRight()));
        assertTrue(containsAll(containingList, anotherSmallerContainedList, (s,t) -> s.getRight() == t));
        assertFalse(containsAll(anotherSmallerContainedList, containingList, (s,t) ->  s == t.getRight()));
        assertFalse(containsAll(containingList, isNotContainedList, (s,t) -> s.getRight() == t));
    }

    @Test
    public void testContainsAllEqualsFunction_withNullLists() {
        assertTrue(containsAll(null, null, (i1, i2) -> i1 == i2));
        assertFalse(containsAll(null, asList(1, 2), (i1, i2) -> i1 == i2));
    }

    @Test
    public void testContainsAll(){
        List<Pair<Integer, String>> containingList = asList(of(1, "two"), of(2, "pair"));
        List<Pair<String, Integer>> containedList = asList(of("two", 8), of("pair", 57));
        List<Pair<String, Integer>> isNotContainedList = asList(of("not present", 8));
        assertTrue(containsAll(containingList, s -> s.getRight(), containedList, s -> s.getLeft()));
        assertFalse(containsAll(containingList, s -> s.getRight(), isNotContainedList, s -> s.getLeft()));
    }

    @Test
    public void testContainsAllNull(){
        List<String> nullList = null;
        List<String> notNullList = asList("two", "pair");
        assertFalse(containsAll(nullList, identity(), notNullList, identity()));
        assertTrue(containsAll(notNullList, identity(), nullList, identity()));
        assertTrue(containsAll(nullList, identity(), nullList, identity()));
        assertTrue(containsAll(notNullList, identity(), notNullList, identity()));
    }

    @Test
    public void testContainsAllNullEntry(){
        List<String> listContainingNull = asList(null, "pair");
        List<String> listWithJustNull = new ArrayList<>();
        listWithJustNull.add(null);
        assertTrue(containsAll(listContainingNull, identity(), listWithJustNull, identity()));
        assertTrue(containsAll(listWithJustNull, identity(), listWithJustNull, identity()));
    }

    @Test
    public void testToSorted(){
        String firstLabel = "getLabel 1";
        String secondLabel = "getLabel 2";
        String firstEntry = "1st entry";
        String secondEntry = "2nd entry";
        String thirdEntry = "3rd entry";
        String fourthEntry = "4th entry";

        List<Pair<String, String>> sortedList = asList(of(firstLabel, firstEntry), of(firstLabel, secondEntry), of(secondLabel, thirdEntry), of(firstLabel, fourthEntry));
        SortedMap<String, List<String>> sortedMap = toSortedMapWithList(sortedList, Pair::getLeft, Pair::getRight);
        assertEquals(2, sortedMap.size());
        assertEquals(firstLabel, sortedMap.firstKey());
        assertEquals(sortedMap.get(firstLabel), asList(firstEntry, secondEntry, fourthEntry));
        assertEquals(secondLabel, sortedMap.lastKey());
        assertEquals(sortedMap.get(secondLabel), asList(thirdEntry));
    }



    @Test
    public void testToSortedNull(){
        List<String> nullList = null;
        SortedMap<String, List<String>> sortedMap = toSortedMapWithList(nullList, identity(), identity());
        assertEquals(new TreeMap<String,List<String>>(), sortedMap);
    }

    @Test
    public void testUnique(){
        List<String> uniqueList = asList("one", "one", "one");
        assertEquals("one", unique(uniqueList, identity()));
        List<String> notUniqueList = asList("one", "two", "three");
        try {
            unique(notUniqueList, identity());
            fail("Should have thrown and IllegalArgumentException");
        } catch (IllegalArgumentException e){
            // Expected
        }
    }

    @Test
    public void simpleMapSet_fromArray() {
        String[] strings = {"a", "b", "c", "c"};
        Set<String> expected = newHashSet(asList("A", "B", "C"));

        Set<String> actual = simpleMapSet(strings, String::toUpperCase);

        assertEquals(expected, actual);
    }

    @Test
    public void test_zip() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);
        List<String> results = new ArrayList<>();

        zip(list1, list2, (string, integer) -> results.add(string + integer));

        assertEquals(asList("a1", "b2", "c3"), results);
    }

    @Test
    public void test_zipWithIndex() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);
        List<String> results = new ArrayList<>();

        zipWithIndex(list1, list2, (string, integer, index) -> results.add(string + integer + "" + index));

        assertEquals(asList("a10", "b21", "c32"), results);
    }

    @Test
    public void test_zipAndMap() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);

        List<String> results = zipAndMap(list1, list2, (string, integer) -> string + integer);

        assertEquals(asList("a1", "b2", "c3"), results);
    }

    @Test
    public void test_zipAndMapWithIndex() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);

        List<String> results = zipAndMapWithIndex(list1, list2, (string, integer, index) -> string + integer + "" + index);

        assertEquals(asList("a10", "b21", "c32"), results);
    }

    @Test
    public void test_simpleFilter_withMap() {

        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");

        Map<Integer, String> filtered = simpleFilter(map, key -> key < 3);

        Map<Integer, String> expectedFilteredMap = new LinkedHashMap<>();
        expectedFilteredMap.put(1, "1");
        expectedFilteredMap.put(2, "2");

        assertEquals(expectedFilteredMap, filtered);
    }

    @Test
    public void test_simpleFilter_withMap_biPredicate() {

        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");

        Map<Integer, String> filtered = simpleFilter(map, (key, value) -> key < 3);

        Map<Integer, String> expectedFilteredMap = new LinkedHashMap<>();
        expectedFilteredMap.put(1, "1");
        expectedFilteredMap.put(2, "2");

        assertEquals(expectedFilteredMap, filtered);
    }

    @Test
    public void test_simpleFilterNot_withMap() {

        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");

        Map<Integer, String> filtered = simpleFilterNot(map, key -> key < 3);

        Map<Integer, String> expectedFilteredMap = new LinkedHashMap<>();
        expectedFilteredMap.put(3, "3");
        expectedFilteredMap.put(4, "4");

        assertEquals(expectedFilteredMap, filtered);
    }

    @Test
    public void test_and() {

        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8);

        Predicate<Integer> compoundPredicate = and(
                i -> i > 3,
                i -> (i % 2) == 0);

        List<Integer> filtered = simpleFilter(list, compoundPredicate);
        assertEquals(asList(4, 6, 8), filtered);
    }

    @Test
    public void test_getOnlyElementOrEmpty() {
        assertEquals(Optional.of(1), getOnlyElementOrEmpty(singletonList(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getOnlyElementOrEmpty_MoreThanOneElement() {
        getOnlyElementOrEmpty(asList(1, 2));
    }

    @Test
    public void test_getOnlyElementOrEmpty_emptyList() {
        assertEquals(Optional.empty(), getOnlyElementOrEmpty(emptyList()));
    }

    @Test
    public void test_getOnlyElementOrEmpty_nullList() {
        assertEquals(Optional.empty(), getOnlyElementOrEmpty(null));
    }

    @Test
    public void test_pairsToMap() {

        Map<Integer, String> expected = new LinkedHashMap<>();
        expected.put(1, "a");
        expected.put(2, "b");

        assertEquals(expected, pairsToMap(asList(Pair.of(1, "a"), Pair.of(2, "b"))));
    }

    @Test
    public void test_simpleFindFirst() {
        assertEquals(Optional.of(3), simpleFindFirst(asList(1, 2, 3), i -> i > 2));
    }

    @Test
    public void test_simpleFindFirst_notFound() {
        assertEquals(Optional.empty(), simpleFindFirst(asList(1, 2, 3), i -> i > 3));
    }

    @Test
    public void test_simpleFindFirst_nullList() {
        assertEquals(Optional.empty(), simpleFindFirst((List<Integer>) null, i -> i > 2));
    }

    @Test
    public void test_simpleFindFirst_withArray() {
        assertEquals(Optional.of(3), simpleFindFirst(new Integer[] {1, 2, 3}, i -> i > 2));
    }

    @Test
    public void test_simpleFindFirst_withNullArray() {
        assertEquals(Optional.empty(), simpleFindFirst((Integer[]) null, i -> i > 2));
    }

    @Test
    public void test_simpleFindFirstMandatory() {
        assertEquals(Integer.valueOf(3), simpleFindFirstMandatory(asList(1, 2, 3), i -> i > 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_simpleFindFirstMandatory_withNullList() {
        simpleFindFirstMandatory((List<Integer>) null, i -> i > 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_simpleFindFirstMandatory_withNoMatchingElement() {
        simpleFindFirstMandatory(asList(1, 2), i -> i > 2);
    }

    @Test
    public void test_simpleAnyMatch() {
        assertTrue(simpleAnyMatch(asList(1, 2, 3), i -> i > 2));
    }

    @Test
    public void test_simpleAnyMatch_noMatch() {
        assertFalse(simpleAnyMatch(asList(1, 2), i -> i > 2));
    }

    @Test
    public void test_simpleAnyMatch_nullList() {
        assertFalse(simpleAnyMatch((List<Integer>) null, i -> i > 2));
    }

    @Test
    public void test_simpleAnyMatch_withArray() {
        assertTrue(simpleAnyMatch(new Integer[] {1, 2, 3}, i -> i > 2));
    }

    @Test
    public void test_simpleAnyMatch_withArray_noMatch() {
        assertFalse(simpleAnyMatch(new Integer[] {1, 2}, i -> i > 2));
    }

    @Test
    public void test_simpleAnyMatch_withArray_nullList() {
        assertFalse(simpleAnyMatch((Integer[]) null, i -> i > 2));
    }

    @Test
    public void test_nullSafe() {
        BinaryOperator<Integer> nullSafeAdder = nullSafe((Integer o1, Integer o2) -> o1 + o2);
        Integer reduction = Stream.of(null, null, null, 4, null, 6, 7).reduce(nullSafeAdder).get();
        assertEquals(17, reduction.intValue());
    }

    @Test
    public void test_asListOfPairs() {

        List<Pair<String, Integer>> expected =
                asList(Pair.of("a", 1), Pair.of("b", 2), Pair.of("c", 3));

        assertEquals(expected, asListOfPairs("a", 1, "b", 2, "c", 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_asListOfPairs_unevenEntries() {
        asListOfPairs("a", 1, "b", 2, "c");
    }

    @Test
    public void test_nOf() {
        assertEquals(asList("a", "a", "a", "a", "a"), nOf(5, "a"));
    }

    @Test
    public void test_flattenOptional() {

        List<Optional<Integer>> listOfOptionals = asList(
                Optional.of(1), Optional.empty(), Optional.of(3), Optional.of(4), Optional.empty());

        assertEquals(asList(1, 3, 4), flattenOptional(listOfOptionals));
    }
}


