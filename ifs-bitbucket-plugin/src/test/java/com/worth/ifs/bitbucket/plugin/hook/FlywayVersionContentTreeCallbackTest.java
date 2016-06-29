package com.worth.ifs.bitbucket.plugin.hook;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.primitives.Ints.asList;
import static com.worth.ifs.bitbucket.plugin.hook.FlywayVersionContentTreeCallback.sortAndFilter;
import static com.worth.ifs.bitbucket.plugin.hook.FlywayVersionContentTreeCallback.versionFromName;
import static junit.framework.Assert.assertEquals;

public class FlywayVersionContentTreeCallbackTest {
    @Test
    public void testSortAndFilter() {
        final List<List<Integer>> unsorted = new ArrayList<>();
        unsorted.add(asList(1, 2, 3));
        unsorted.add(asList(1));
        unsorted.add(asList());
        unsorted.add(asList(2, 3, 5));
        unsorted.add(asList(2, 3, 5));
        unsorted.add(asList(2, 5));
        unsorted.add(asList(10));
        final List<List<Integer>> sortedAndFiltered = sortAndFilter(unsorted);
        assertEquals(asList(1), sortedAndFiltered.get(0));
        assertEquals(asList(1, 2, 3), sortedAndFiltered.get(1));
        assertEquals(asList(2, 3, 5), sortedAndFiltered.get(2));
        assertEquals(asList(2, 3, 5), sortedAndFiltered.get(3));
        assertEquals(asList(2, 5), sortedAndFiltered.get(4));
        assertEquals(asList(10), sortedAndFiltered.get(5));
    }

    @Test
    public void testVersionFromName(){
        assertEquals(asList(12,22,1,7), versionFromName("V12_22_1_7__test.sql"));
        assertEquals(asList(12), versionFromName("V12__test.sql"));
        assertEquals(asList(12), versionFromName("V12__test__some_more.sql"));
        assertEquals(asList(), versionFromName("12__not_valid.sql"));
        assertEquals(asList(), versionFromName("V12_22_not_valid.sql"));
        assertEquals(asList(), versionFromName("V12_22__not_valid.java"));
    }
}