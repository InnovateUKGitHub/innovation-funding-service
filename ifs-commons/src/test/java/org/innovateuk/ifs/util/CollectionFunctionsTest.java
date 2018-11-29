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
import static java.util.Optional.empty;
import static java.util.function.Function.identity;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.junit.Assert.*;

/**
 *
 */
public class CollectionFunctionsTest {

    @Test
    public void simpleGroupBy(){
        Map<String, String> map = new HashMap<>();
        map.put("formInput[1].MMYYYY.year", "2001");
        map.put("formInput[1].MMYYYY.month", "11");
        map.put("formInput[2].MMYYYY.year", "1968");
        map.put("formInput[2].MMYYYY.month", "2");
        map.put("formInput[3].MMYYYY.year", "2001");
        map.put("formInput[3].MMYYYY.month", "11");

        // Method under test.
        Map<String, Map<String, String>> simpleGroupBy =
                CollectionFunctions.simpleGroupBy(map, key -> key.replace(".MMYYYY.month", "").replace(".MMYYYY.year", ""));

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
    public void simpleGroupByNull(){
        Map<String, String> map = new HashMap<>();
        map.put("formInput[1].MMYYYY.year", null);
        map.put("formInput[1].MMYYYY.month", null);

        // Method under test.
        Map<String, Map<String, String>> simpleGroupBy =
                CollectionFunctions.simpleGroupBy(map, key -> key.replace(".MMYYYY.month", "").replace(".MMYYYY.year", ""));

        assertEquals(1, simpleGroupBy.size());
        // formInput[1]
        assertNotNull(simpleGroupBy.get("formInput[1]"));
        assertNull(simpleGroupBy.get("formInput[1]").get("formInput[1].MMYYYY.year"));
        assertNull(simpleGroupBy.get("formInput[1]").get("formInput[1].MMYYYY.month"));
    }


    @Test
    public void flattenLists() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), asList(4, 5), singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 4, 5, 6);

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void flattenLists_nullSafe() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), null, singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void flattenLists_emptyElements() {

        List<List<Integer>> deepList = asList(asList(1, 2, 3), emptyList(), singletonList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void flattenLists_allEmpty() {

        List<List<Integer>> deepList = asList(emptyList(), emptyList(), emptyList());
        List<Integer> expectedFlatList = emptyList();

        assertEquals(expectedFlatList, CollectionFunctions.flattenLists(deepList));
    }

    @Test
    public void flattenSets() {

        Set<Set<Integer>> deepList = CollectionFunctions.asLinkedSet(
                        CollectionFunctions.asLinkedSet(1, 2, 3),
                        CollectionFunctions.asLinkedSet(4, 5),
                        singleton(6));

        Set<Integer> expectedFlatList = CollectionFunctions.asLinkedSet(1, 2, 3, 4, 5, 6);

        assertEquals(expectedFlatList, CollectionFunctions.flattenSets(deepList));
    }

    @Test
    public void combineLists() {

        List<Integer> list1 = asList(1, 2, 3);
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);

        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void combineLists_nullSafe() {

        List<Integer> list1 = null;
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(4, 5, 6);

        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void combineLists_emptyElements() {

        List<Integer> list1 = emptyList();
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = singletonList(6);

        List<Integer> expectedCombinedList = asList(4, 5, 6);

        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void combineListsWithListAndVarargs() {

        List<Integer> list = asList(1, 2, 3);
        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(list, 4, 5, 6));
    }

    @Test
    public void combineListsWithListAndVarargsNullSafe() {
        List<Integer> expectedCombinedList = asList(4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(null, 4, 5, 6));
    }

    @Test
    public void combineListsWithElementAndVarargs() {
        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void combineListsWithElementAndVarargsEmptyVarargs() {
        List<Integer> expectedCombinedList = singletonList(1);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(1));
    }

    @Test
    public void combineListsWithElementAndVarargsNullElementSafe() {
        List<Integer> expectedCombinedList = asList(2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(null, 2, 3, 4, 5, 6));
    }

    @Test
    public void combineListsWithElementAndList() {

        List<Integer> list = asList(1, 2, 3);

        List<Integer> expectedCombinedList = asList(0, 1, 2, 3);
        assertEquals(expectedCombinedList, CollectionFunctions.combineLists(0, list));
    }

    @Test
    public void forEachWithIndex() {

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
    public void forEachWithIndex_nullSafe() {

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
    public void forEachWithIndex_emptyValues() {

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
    public void mapWithIndex() {

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
    public void mapWithIndex_nullSafe() {

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
    public void mapWithIndex_emptyElements() {

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
    public void reverse() {

        List<String> originalList = asList("string 1", "another string 2", "string 3");
        List<String> reversed = CollectionFunctions.reverse(originalList);
        assertEquals(asList("string 3", "another string 2", "string 1"), reversed);
    }

    @Test
    public void reverse_nullElements() {

        List<String> originalList = asList(null, null, "string 3");
        List<String> reversed = CollectionFunctions.reverse(originalList);
        assertEquals(asList("string 3", null, null), reversed);
    }

    @Test
    public void reverse_nullSafe() {
        assertEquals(new ArrayList<>(), CollectionFunctions.reverse(null));
    }

    @Test
    public void reverse_empty() {
        assertEquals(new ArrayList<>(), CollectionFunctions.reverse(new ArrayList<>()));
    }

    @Test
    public void getOnlyElement() {
        assertEquals("hi there", CollectionFunctions.getOnlyElement(singletonList("hi there")));
    }

    @Test
    public void nullElement() {
        List<Object> nullElementList = new ArrayList<>();
        nullElementList.add(null);
        assertNull(CollectionFunctions.getOnlyElement(nullElementList));
    }

    @Test
    public void getOnlyElement_tooManyElements() {
        try {
            CollectionFunctions.getOnlyElement(asList("hi there", "goodbye!"));
            fail("Should have thrown an IllegalArgumentException as there were too many elements");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void getOnlyElement_notEnoughElements() {
        try {
            CollectionFunctions.getOnlyElement(emptyList());
            fail("Should have thrown an IllegalArgumentException as there weren't enough elements");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void getOnlyElement_notEnoughElements_nullList() {
        try {
            CollectionFunctions.getOnlyElement(null);
            fail("Should have thrown an IllegalArgumentException as there weren't enough elements (null list)");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }
    }

    @Test
    public void simpleMap() {
        assertEquals(asList("123 string", "456 string"), CollectionFunctions.simpleMap(asList(123, 456), i -> i + " string"));
    }

    @Test
    public void simpleMap_nullList() {
        assertEquals(emptyList(), CollectionFunctions.simpleMap((List<?>) null, i -> i + " string"));
    }

    @Test
    public void simpleMap_nullElements() {
        assertEquals(asList("123 string", "null string"), CollectionFunctions.simpleMap(asList(123, null), i -> i + " string"));
    }

    @Test
    public void simpleMap_withArray() {
        assertEquals(asList("123 string", "456 string"), CollectionFunctions.simpleMap(new Integer[] {123, 456}, i -> i + " string"));
    }

    @Test
    public void simpleMap_withMap() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        assertEquals(asList("1a", "2b"), CollectionFunctions.simpleMap(map, (key, value) -> key + value));
    }

    @Test
    public void simpleMapKey() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        Map<Integer, String> expected = new HashMap<>();
        expected.put(2, "a");
        expected.put(4, "b");

        assertEquals(expected, CollectionFunctions.simpleMapKey(map, key -> key * 2));
    }

    @Test
    public void simpleMapValue() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        Map<Integer, String> expected = new HashMap<>();
        expected.put(1, "a!");
        expected.put(2, "b!");

        assertEquals(expected, CollectionFunctions.simpleMapValue(map, value -> value  + "!"));
    }

    @Test
    public void simpleMapEntry() {

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");

        Map<String, String> expected = new HashMap<>();
        expected.put("2a!", "4a?");
        expected.put("4b!", "8b?");

        assertEquals(expected, CollectionFunctions.simpleMapEntry(map,
                entry -> entry.getKey() * 2 + entry.getValue() + "!",
                entry -> entry.getKey() * 4 + entry.getValue() + "?"));
    }

    @Test
    public void simpleMapWithIndex() {
        assertEquals(asList("123 string 0", "456 string 1"),
                CollectionFunctions.simpleMapWithIndex(asList(123, 456), (element, index) -> element + " string " + index));
    }

    @Test
    public void simpleMapWithIndex_emptyList() {
        assertEquals(emptyList(),
                CollectionFunctions.simpleMapWithIndex(emptyList(), (element, index) -> element + " string " + index));
    }

    @Test
    public void simpleMapWithIndex_withArray() {
        assertEquals(asList("123 string 0", "456 string 1"),
                CollectionFunctions.simpleMapWithIndex(new Integer[] {123, 456}, (element, index) -> element + " string " + index));
    }

    @Test
    public void simpleMapArray() {
        assertEquals(new String[] {"123 string", "456 string"},
                CollectionFunctions.simpleMapArray(new Integer[] {123, 456}, element -> element + " string", String.class));
    }

    @Test
    public void simpleMapArray_nullArray() {
        assertEquals(new String[] {},
                CollectionFunctions.simpleMapArray(null, element -> element + " string ", String.class));
    }

    @Test
    public void simpleMapArray_emptyArray() {
        assertEquals(new String[] {},
                CollectionFunctions.simpleMapArray(new String[] {}, element -> element + " string ", String.class));
    }

    @Test
    public void simpleMapSet() {
        assertEquals(CollectionFunctions.asLinkedSet("123 string", "456 string"),
                CollectionFunctions.simpleMapSet(CollectionFunctions.asLinkedSet(123, 456), i -> i + " string"));
    }

    @Test
    public void simpleMapSet_null() {
        assertEquals(emptySet(), CollectionFunctions.simpleMapSet((Set<?>) null, i -> i + " string"));
    }

    @Test
    public void simpleMapSet_withList() {
        assertEquals(CollectionFunctions.asLinkedSet("123 string", "456 string"),
                CollectionFunctions.simpleMapSet(asList(123, 456), i -> i + " string"));
    }

    @Test
    public void simpleMapSet_withEmptyList() {
        assertEquals(emptySet(), CollectionFunctions.simpleMapSet(emptyList(), i -> i + " string"));
    }

    @Test
    public void simpleMapSet_withArray() {
        assertEquals(CollectionFunctions.asLinkedSet("123 string", "456 string"),
                CollectionFunctions.simpleMapSet(new Integer[] {123, 456}, i -> i + " string"));
    }

    @Test
    public void simpleMapSet_withEmptyArray() {
        assertEquals(emptySet(), CollectionFunctions.simpleMapSet(new Integer[] {}, i -> i + " string"));
    }

    @Test
    public void simpleToLinkedHashSetWithCollection() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "456 string")),
                CollectionFunctions.simpleToLinkedHashSet(asList(123, 456), i -> i + " string"));
    }

    @Test
    public void simpleToLinkedHashSetWithCollection_nullList() {
        assertEquals(new LinkedHashSet<String>(),
                CollectionFunctions.simpleToLinkedHashSet((List<?>) null, i -> i + " string"));
    }

    @Test
    public void arrayToLinkedHashSetWithCollection_nullElements() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "null string")),
                CollectionFunctions.simpleToLinkedHashSet(asList(123, null), i -> i + " string"));
    }

    @Test
    public void toLinkedHashSetWithArray() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "456 string")),
                CollectionFunctions.simpleToLinkedHashSet(new Integer[]{123, 456}, i -> i + " string"));
    }

    @Test
    public void arrayToLinkedHashSetWithArray_nullArray() {
        String[] stringArray = null;

        assertEquals(new LinkedHashSet<String>(),
                CollectionFunctions.simpleToLinkedHashSet(stringArray, i -> i + " string"));
    }

    @Test
    public void simpleToLinkedHashSetWithArray_nullElements() {
        assertEquals(new LinkedHashSet<>(asList("123 string", "null string")),
                CollectionFunctions.simpleToLinkedHashSet(new Integer[]{123, null}, i -> i + " string"));
    }

    @Test
    public void simpleFilter() {
        assertEquals(singletonList(789), CollectionFunctions.simpleFilter(asList(123, 456, 789), i -> i > 456));
    }

    @Test
    public void simpleFilter_nullList() {
        assertEquals(emptyList(), CollectionFunctions.simpleFilter((Collection<?>)null, i -> false));
    }

    @Test
    public void simpleFilter_nullElements() {
        assertEquals(singletonList(789), CollectionFunctions.simpleFilter(asList(123, null, 456, 789), i -> i != null && i > 456));
    }

    @Test
    public void simpleFilterNot() {
        assertEquals(asList(123, 456), CollectionFunctions.simpleFilterNot(asList(123, 456, 789), i -> i > 456));
    }

    @Test
    public void simpleFilterNot_nullList() {
        assertEquals(emptyList(), CollectionFunctions.simpleFilterNot((List<?>)null, i -> false));
    }

    @Test
    public void simpleFilterNot_nullElements() {
        assertEquals(asList(123, null, 456), CollectionFunctions.simpleFilterNot(asList(123, null, 456, 789), i -> i != null && i > 456));
    }

    @Test
    public void simpleFindAnyWithNullList() {
        assertEquals(empty(), CollectionFunctions.simpleFindAny(null, i -> true));
    }

    @Test
    public void simpleFindAnyWithNullElements() {
        assertEquals(Optional.of(789), CollectionFunctions.simpleFindAny(asList(123, null, 455, 789), i -> i != null && i > 456));
    }

    @Test
    public void simpleFindAny() {
        assertEquals(Optional.of(789), CollectionFunctions.simpleFindAny(asList(123, 789, 455, 112), i -> i != null && i > 456));
    }

    @Test
    public void simpleFilter_withArray() {
        assertEquals(singletonList(789), CollectionFunctions.simpleFilter(new Integer[] {123, 456, 789}, i -> i > 456));
    }

    @Test
    public void simpleFilter_withEmptyArray() {
        assertEquals(emptyList(), CollectionFunctions.simpleFilter((Integer[]) null, i -> i > 456));
    }

    @Test
    public void testToLinkedMap() {
        List<String> orderedList = asList("1", "2", "3", "4", "5");
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
        List<String> orderedList = asList("1", "2", "3", "4", "5", "4");
        orderedList.stream().collect(CollectionFunctions.toLinkedMap(item -> item, item -> item + item));
    }

    @Test
    public void simpleJoiner() {
        assertEquals("123, 456, 789", CollectionFunctions.simpleJoiner(asList(123, 456, 789), ", "));
    }

    @Test
    public void simpleJoiner_nullList() {
        assertEquals("", CollectionFunctions.simpleJoiner(null, ", "));
    }

    @Test
    public void simpleJoiner_nullElements() {
        assertEquals("123, , 789", CollectionFunctions.simpleJoiner(asList(123, null, 789), ", "));
    }

    @Test
    public void simpleJoiner_withTransformer() {
        assertEquals("123!, 456!, 789!", CollectionFunctions.simpleJoiner(asList(123, 456, 789), i -> i + "!", ", "));
    }

    @Test
    public void simpleJoiner_withTransformer_nullList() {
        assertEquals("", CollectionFunctions.simpleJoiner(null, i -> i + "!", ", "));
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

        Map<Integer, Integer> toLinkedMap = CollectionFunctions.simpleToLinkedMap(null, identity(), identity());
        assertTrue(toLinkedMap.isEmpty());
    }

    @Test
    public void simpleToLinkedMapWhenInputListIsEmpty() {

        Map<Integer, Integer> toLinkedMap = CollectionFunctions.simpleToLinkedMap(emptyList(), identity(), identity());
        assertTrue(toLinkedMap.isEmpty());
    }

    @Test
    public void simpleToLinkedMap() {

        List<Integer> inputList = asList(1, 2, 3, 4, 5, 6);
        Map<Integer, String> toLinkedMap =
                CollectionFunctions.simpleToLinkedMap(inputList, element -> element + 10, element -> element + " value");

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
    public void simpleLinkedMapKeyAndValue() {

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "Test1");
        inputMap.put(2, "Test2");
        inputMap.put(3, "Test3");

        Map<Integer, String> toLinkedMap =
                CollectionFunctions.simpleLinkedMapKeyAndValue(inputMap, element -> element + 10, element -> element + " value");

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
    public void simpleLinkedMapKey() {

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "Test1");
        inputMap.put(2, "Test2");
        inputMap.put(3, "Test3");

        Map<Integer, String> toLinkedMap = CollectionFunctions.simpleLinkedMapKey(inputMap, element -> element + 10);

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
    public void simpleLinkedMapValue() {

        Map<Integer, String> inputMap = new LinkedHashMap<>();
        inputMap.put(1, "Test1");
        inputMap.put(2, "Test2");
        inputMap.put(3, "Test3");

        Map<Integer, String> toLinkedMap = CollectionFunctions.simpleLinkedMapValue(inputMap, element -> element + " value");

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
        assertEquals(3 * 2, permutations.size());

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
    public void removeElement() {
        List<String> tokens = asList("a", "b", "c");
        assertEquals(asList("a", "c"), CollectionFunctions.removeElement(tokens, 1));
        assertEquals(asList("a", "b", "c"), tokens);
    }

    @Test
    public void removeDuplicates() {
        List<String> tokens = asList("a", "b", "c", "b", "e", "c");
        assertEquals(asList("a", "b", "c", "e"), CollectionFunctions.removeDuplicates(tokens));
        assertEquals(asList("a", "b", "c", "b", "e", "c"), tokens);
    }

    @Test
    public void sort() {
        assertEquals(asList("a", "b", "c"), CollectionFunctions.sort(asList("b", "c", "a")));
    }

    @Test
    public void sort_nullSafe() {
        assertEquals(emptyList(), CollectionFunctions.sort(null));
    }

    @Test
    public void sortWithComparator() {
        assertEquals(asList("c", "b", "a"), CollectionFunctions.sort(asList("b", "c", "a"), Comparator.reverseOrder()));
    }

    @Test
    public void sortWithComparator_nullSafe() {
        assertEquals(emptyList(), CollectionFunctions.sort((List<String>) null, Comparator.reverseOrder()));
    }

    @Test
    public void testSimplePartition() {
        Pair<List<String>, List<String>> partitioned = CollectionFunctions.simplePartition(asList("a1", "b1", "a2", "c1"), string -> string.startsWith("a"));
        assertEquals(asList("a1", "a2"), partitioned.getLeft());
        assertEquals(asList("b1", "c1"), partitioned.getRight());
    }

    @Test
    public void testSimplePartition_NullSafe() {
        Pair<List<String>, List<String>> partitioned = CollectionFunctions.simplePartition(null, string -> string.startsWith("a"));
        assertEquals(emptyList(), partitioned.getLeft());
        assertEquals(emptyList(), partitioned.getRight());
    }


    @Test
    public void testContainsAllEqualsFunction(){
        List<Pair<Integer, String>> containingList = asList(of(1, "two"), of(2, "pair"));
        List<String> containedList = asList("two", "pair");
        List<String> anotherSmallerContainedList = singletonList("two");
        List<String> isNotContainedList = singletonList("not present");
        assertTrue(containsAll(containingList, containedList, (s,t) -> Objects.equals(s.getRight(), t)));
        assertTrue(containsAll(containedList,containingList, (s,t) -> Objects.equals(s, t.getRight())));
        assertTrue(containsAll(containingList, anotherSmallerContainedList, (s,t) -> Objects.equals(s.getRight(), t)));
        assertFalse(containsAll(anotherSmallerContainedList, containingList, (s,t) -> Objects.equals(s, t.getRight())));
        assertFalse(containsAll(containingList, isNotContainedList, (s,t) -> Objects.equals(s.getRight(), t)));
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
        List<Pair<String, Integer>> isNotContainedList = singletonList(of("not present", 8));
        assertTrue(containsAll(containingList, Pair::getRight, containedList, Pair::getLeft));
        assertFalse(containsAll(containingList, Pair::getRight, isNotContainedList, Pair::getLeft));
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
        assertEquals(sortedMap.get(secondLabel), singletonList(thirdEntry));
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

        Set<String> actual = CollectionFunctions.simpleMapSet(strings, String::toUpperCase);

        assertEquals(expected, actual);
    }

    @Test
    public void zip() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);
        List<String> results = new ArrayList<>();

        CollectionFunctions.zip(list1, list2, (string, integer) -> results.add(string + integer));

        assertEquals(asList("a1", "b2", "c3"), results);
    }

    @Test
    public void zipWithIndex() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);
        List<String> results = new ArrayList<>();

        CollectionFunctions.zipWithIndex(list1, list2, (string, integer, index) -> results.add(string + integer + "" + index));

        assertEquals(asList("a10", "b21", "c32"), results);
    }

    @Test
    public void zipAndMap() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);

        List<String> results = CollectionFunctions.zipAndMap(list1, list2, (string, integer) -> string + integer);

        assertEquals(asList("a1", "b2", "c3"), results);
    }

    @Test
    public void zipAndMapWithIndex() {

        List<String> list1 = asList("a", "b", "c");
        List<Integer> list2 = asList(1, 2, 3);

        List<String> results =
                CollectionFunctions.zipAndMapWithIndex(list1, list2, (string, integer, index) -> string + integer + "" + index);

        assertEquals(asList("a10", "b21", "c32"), results);
    }

    @Test
    public void simpleFilter_withMap() {

        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");

        Map<Integer, String> filtered = CollectionFunctions.simpleFilter(map, key -> key < 3);

        Map<Integer, String> expectedFilteredMap = new LinkedHashMap<>();
        expectedFilteredMap.put(1, "1");
        expectedFilteredMap.put(2, "2");

        assertEquals(expectedFilteredMap, filtered);
    }

    @Test
    public void simpleFilter_withMap_biPredicate() {

        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");

        Map<Integer, String> filtered = CollectionFunctions.simpleFilter(map, (key, value) -> key < 3);

        Map<Integer, String> expectedFilteredMap = new LinkedHashMap<>();
        expectedFilteredMap.put(1, "1");
        expectedFilteredMap.put(2, "2");

        assertEquals(expectedFilteredMap, filtered);
    }

    @Test
    public void simpleFilterNot_withMap() {

        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");

        Map<Integer, String> filtered = CollectionFunctions.simpleFilterNot(map, key -> key < 3);

        Map<Integer, String> expectedFilteredMap = new LinkedHashMap<>();
        expectedFilteredMap.put(3, "3");
        expectedFilteredMap.put(4, "4");

        assertEquals(expectedFilteredMap, filtered);
    }

    @Test
    public void and() {

        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8);

        Predicate<Integer> compoundPredicate = CollectionFunctions.and(
                i -> i > 3,
                i -> (i % 2) == 0);

        List<Integer> filtered = CollectionFunctions.simpleFilter(list, compoundPredicate);
        assertEquals(asList(4, 6, 8), filtered);
    }

    @Test
    public void getOnlyElementOrEmpty() {
        assertEquals(Optional.of(1), CollectionFunctions.getOnlyElementOrEmpty(singletonList(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOnlyElementOrEmpty_MoreThanOneElement() {
        CollectionFunctions.getOnlyElementOrEmpty(asList(1, 2));
    }

    @Test
    public void getOnlyElementOrEmpty_emptyList() {
        assertEquals(Optional.empty(), CollectionFunctions.getOnlyElementOrEmpty(emptyList()));
    }

    @Test
    public void getOnlyElementOrEmpty_nullList() {
        assertEquals(Optional.empty(), CollectionFunctions.getOnlyElementOrEmpty(null));
    }

    @Test
    public void pairsToMap() {

        Map<Integer, String> expected = new LinkedHashMap<>();
        expected.put(1, "a");
        expected.put(2, "b");

        assertEquals(expected, CollectionFunctions.pairsToMap(asList(Pair.of(1, "a"), Pair.of(2, "b"))));
    }

    @Test
    public void simpleFindFirst() {
        assertEquals(Optional.of(3), CollectionFunctions.simpleFindFirst(asList(1, 2, 3), i -> i > 2));
    }

    @Test
    public void simpleFindFirst_notFound() {
        assertEquals(Optional.empty(), CollectionFunctions.simpleFindFirst(asList(1, 2, 3), i -> i > 3));
    }

    @Test
    public void simpleFindFirst_nullList() {
        assertEquals(Optional.empty(), CollectionFunctions.simpleFindFirst((List<Integer>) null, i -> i > 2));
    }

    @Test
    public void simpleFindFirst_withArray() {
        assertEquals(Optional.of(3), CollectionFunctions.simpleFindFirst(new Integer[] {1, 2, 3}, i -> i > 2));
    }

    @Test
    public void simpleFindFirst_withNullArray() {
        assertEquals(Optional.empty(), CollectionFunctions.simpleFindFirst((Integer[]) null, i -> i > 2));
    }

    @Test
    public void simpleFindFirstMandatory() {
        assertEquals(Integer.valueOf(3), CollectionFunctions.simpleFindFirstMandatory(asList(1, 2, 3), i -> i > 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleFindFirstMandatory_withNullList() {
        CollectionFunctions.simpleFindFirstMandatory((List<Integer>) null, i -> i > 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleFindFirstMandatory_withNoMatchingElement() {
        CollectionFunctions.simpleFindFirstMandatory(asList(1, 2), i -> i > 2);
    }

    @Test
    public void simpleAnyMatch() {
        assertTrue(CollectionFunctions.simpleAnyMatch(asList(1, 2, 3), i -> i > 2));
    }

    @Test
    public void simpleAnyMatch_noMatch() {
        assertFalse(CollectionFunctions.simpleAnyMatch(asList(1, 2), i -> i > 2));
    }

    @Test
    public void simpleAnyMatch_nullList() {
        assertFalse(CollectionFunctions.simpleAnyMatch((List<Integer>) null, i -> i > 2));
    }

    @Test
    public void simpleAnyMatch_withArray() {
        assertTrue(CollectionFunctions.simpleAnyMatch(new Integer[] {1, 2, 3}, i -> i > 2));
    }

    @Test
    public void simpleAnyMatch_withArray_noMatch() {
        assertFalse(CollectionFunctions.simpleAnyMatch(new Integer[] {1, 2}, i -> i > 2));
    }

    @Test
    public void simpleAnyMatch_withArray_nullArray() {
        assertFalse(CollectionFunctions.simpleAnyMatch((Integer[]) null, i -> i > 2));
    }

    @Test
    public void simpleAllMatch() {
        assertTrue(CollectionFunctions.simpleAllMatch(asList(1, 2, 3), i -> i > 0));
    }

    @Test
    public void simpleAllMatch_noMatch() {
        assertFalse(CollectionFunctions.simpleAllMatch(asList(1, 2, 3), i -> i > 1));
    }

    @Test
    public void simpleAllMatch_nullList() {
        assertTrue(CollectionFunctions.simpleAllMatch((List<Integer>) null, i -> i > 2));
    }

    @Test
    public void simpleAllMatch_emptyList() {
        assertTrue(CollectionFunctions.simpleAllMatch(emptyList(), i -> i != null));
    }

    @Test
    public void simpleAllMatch_withArray() {
        assertTrue(CollectionFunctions.simpleAllMatch(new Integer[] {1, 2, 3}, i -> i > 0));
    }

    @Test
    public void simpleAllMatch_withArray_noMatch() {
        assertFalse(CollectionFunctions.simpleAllMatch(new Integer[] {1, 2}, i -> i > 1));
    }

    @Test
    public void simpleAllMatch_withArray_nullArray() {
        assertTrue(CollectionFunctions.simpleAllMatch((Integer[]) null, i -> i > 2));
    }

    @Test
    public void nullSafe() {
        BinaryOperator<Integer> nullSafeAdder = CollectionFunctions.nullSafe((Integer o1, Integer o2) -> o1 + o2);
        Integer reduction = Stream.of(null, null, null, 4, null, 6, 7).reduce(nullSafeAdder).get();
        assertEquals(17, reduction.intValue());
    }

    @Test
    public void asListOfPairs() {

        List<Pair<String, Integer>> expected =
                asList(Pair.of("a", 1), Pair.of("b", 2), Pair.of("c", 3));

        assertEquals(expected, CollectionFunctions.asListOfPairs("a", 1, "b", 2, "c", 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void asListOfPairs_unevenEntries() {
        CollectionFunctions.asListOfPairs("a", 1, "b", 2, "c");
    }

    @Test
    public void nOf() {
        assertEquals(asList("a", "a", "a", "a", "a"), CollectionFunctions.nOf(5, "a"));
    }

    @Test
    public void flattenOptional() {

        List<Optional<Integer>> listOfOptionals = asList(
                Optional.of(1), Optional.empty(), Optional.of(3), Optional.of(4), Optional.empty());

        assertEquals(asList(1, 3, 4), CollectionFunctions.flattenOptional(listOfOptionals));
    }
}


