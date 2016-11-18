package com.worth.ifs.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.*;
import java.util.Map.Entry;

import static com.worth.ifs.util.CollectionFunctions.containsAll;
import static com.worth.ifs.util.CollectionFunctions.toSortedMap;
import static com.worth.ifs.util.CollectionFunctions.unique;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static org.apache.commons.lang3.tuple.Pair.of;
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
        assertEquals(asList(), CollectionFunctions.simpleMap((List<?>) null, i -> i + " string"));
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

    @Test
    public void simpleToLinkedMapWhenInputListIsNull() {

        Map<Integer, Integer> toLinkedMap = CollectionFunctions.simpleToLinkedMap(null, identity(), identity());
        assertTrue(toLinkedMap.isEmpty());
    }

    @Test
    public void simpleToLinkedMapWhenInputListIsEmpty() {

        Map<Integer, Integer> toLinkedMap = CollectionFunctions.simpleToLinkedMap(Collections.EMPTY_LIST, identity(), identity());
        assertTrue(toLinkedMap.isEmpty());
    }

    @Test
    public void simpleToLinkedMap() {

        List<Integer> inputList = Arrays.asList(1, 2, 3, 4, 5, 6);
        Map<Integer, String> toLinkedMap = CollectionFunctions.simpleToLinkedMap(inputList, element -> element + 10, element -> element + " value");

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

        Map<Integer, String> toLinkedMap = CollectionFunctions.simpleLinkedMapKeyAndValue(inputMap, element -> element + 10, element -> element + " value");

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
    public void testRemoveElement() {
        List<String> tokens = asList("a", "b", "c");
        assertEquals(asList("a", "c"), CollectionFunctions.removeElement(tokens, 1));
        assertEquals(asList("a", "b", "c"), tokens);
    }

    @Test
    public void testRemoveDuplicates() {
        List<String> tokens = asList("a", "b", "c", "b", "e", "c");
        assertEquals(asList("a", "b", "c", "e"), CollectionFunctions.removeDuplicates(tokens));
        assertEquals(asList("a", "b", "c", "b", "e", "c"), tokens);
    }

    @Test
    public void testAsLinkedSet() {
        List<String> tokens = asList("a", "b", "c", "b", "e", "c");
        Set<String> expectedNewSet = new LinkedHashSet<>();
        asList("a", "b", "c", "e").forEach(token -> expectedNewSet.add(token));
        assertEquals(asList("a", "b", "c", "b", "e", "c"), tokens);
    }

    @Test
    public void test_sort() {
        assertEquals(asList("a", "b", "c"), CollectionFunctions.sort(asList("b", "c", "a")));
    }

    @Test
    public void test_sort_nullSafe() {
        assertEquals(emptyList(), CollectionFunctions.sort(null));
    }

    @Test
    public void test_sortWithComparator() {
        assertEquals(asList("c", "b", "a"), CollectionFunctions.sort(asList("b", "c", "a"), (s1, s2) -> s2.compareTo(s1)));
    }

    @Test
    public void test_sortWithComparator_nullSafe() {
        assertEquals(emptyList(), CollectionFunctions.sort((List<String>) null, (s1, s2) -> s2.compareTo(s1)));
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
        List<String> anotherSmallerContainedList = asList("two");
        List<String> isNotContainedList = asList("not present");
        assertTrue(containsAll(containingList, containedList, (s,t) -> s.getRight() == t));
        assertTrue(containsAll(containedList,containingList, (s,t) ->  s == t.getRight()));
        assertTrue(containsAll(containingList, anotherSmallerContainedList, (s,t) -> s.getRight() == t));
        assertFalse(containsAll(anotherSmallerContainedList, containingList, (s,t) ->  s == t.getRight()));
        assertFalse(containsAll(containingList, isNotContainedList, (s,t) -> s.getRight() == t));
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
        SortedMap<String, List<String>> sortedMap = toSortedMap(sortedList, Pair::getLeft, Pair::getRight);
        assertEquals(2, sortedMap.size());
        assertEquals(firstLabel, sortedMap.firstKey());
        assertEquals(sortedMap.get(firstLabel), asList(firstEntry, secondEntry, fourthEntry));
        assertEquals(secondLabel, sortedMap.lastKey());
        assertEquals(sortedMap.get(secondLabel), asList(thirdEntry));
    }



    @Test
    public void testToSortedNull(){
        List<String> nullList = null;
        SortedMap<String, List<String>> sortedMap = toSortedMap(nullList, identity(), identity());
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
}


