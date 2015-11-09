package com.worth.ifs.util;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class IfsFunctionsTest {

    @Test
    public void test_flattenLists() {
        List<List<Integer>> deepList = asList(asList(1, 2, 3), asList(4, 5), asList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedFlatList, IfsFunctions.flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_nullSafe() {
        List<List<Integer>> deepList = asList(asList(1, 2, 3), null, asList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);
        assertEquals(expectedFlatList, IfsFunctions.flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_emptyElements() {
        List<List<Integer>> deepList = asList(asList(1, 2, 3), asList(), asList(6));
        List<Integer> expectedFlatList = asList(1, 2, 3, 6);
        assertEquals(expectedFlatList, IfsFunctions.flattenLists(deepList));
    }

    @Test
    public void test_flattenLists_allEmpty() {
        List<List<Integer>> deepList = asList(asList(), asList(), asList());
        List<Integer> expectedFlatList = asList();
        assertEquals(expectedFlatList, IfsFunctions.flattenLists(deepList));
    }

    @Test
    public void test_combineLists() {
        List<Integer> list1 = asList(1, 2, 3);
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = asList(6);
        List<Integer> expectedCombinedList = asList(1, 2, 3, 4, 5, 6);
        assertEquals(expectedCombinedList, IfsFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineLists_nullSafe() {
        List<Integer> list1 = null;
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = asList(6);
        List<Integer> expectedCombinedList = asList(4, 5, 6);
        assertEquals(expectedCombinedList, IfsFunctions.combineLists(list1, list2, list3));
    }

    @Test
    public void test_combineLists_emptyElements() {
        List<Integer> list1 = asList();
        List<Integer> list2 = asList(4, 5);
        List<Integer> list3 = asList(6);
        List<Integer> expectedCombinedList = asList(4, 5, 6);
        assertEquals(expectedCombinedList, IfsFunctions.combineLists(list1, list2, list3));
    }
}
