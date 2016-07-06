package com.worth.ifs.bitbucket.plugin.hook;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.primitives.Ints.asList;
import static com.worth.ifs.bitbucket.plugin.hook.FlywayVersionContentTreeCallback.sortAndFilter;
import static com.worth.ifs.bitbucket.plugin.hook.FlywayVersionContentTreeCallback.versionFromName;
import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;
import static org.apache.commons.lang3.tuple.Pair.of;

public class FlywayVersionContentTreeCallbackTest {
    @Test
    public void testSortAndFilter() {
        final List<Pair<String, List<Integer>>> unsorted = new ArrayList<Pair<String, List<Integer>>>();
        unsorted.add(of("V1_2_3__Patch.sql", asList(1, 2, 3)));
        unsorted.add(of("V1__Patch.sql", asList(1)));
        unsorted.add(of("NotAPatch.java", asList()));
        unsorted.add(of("V2_3_5__Patch.sql", asList(2, 3, 5)));
        unsorted.add(of("V2_3_5__Patch.sql", asList(2, 3, 5)));
        unsorted.add(of("V2_5__Patch.sql", asList(2, 5)));
        unsorted.add(of("V10__Patch.sql", asList(10)));
        final List<Pair<String, List<Integer>>> sortedAndFiltered = sortAndFilter(unsorted);
        assertEquals(of("V1__Patch.sql", asList(1)), sortedAndFiltered.get(0));
        assertEquals(of("V1_2_3__Patch.sql", asList(1, 2, 3)), sortedAndFiltered.get(1));
        assertEquals(of("V2_3_5__Patch.sql", asList(2, 3, 5)), sortedAndFiltered.get(2));
        assertEquals(of("V2_3_5__Patch.sql", asList(2, 3, 5)), sortedAndFiltered.get(3));
        assertEquals(of("V2_5__Patch.sql", asList(2, 5)), sortedAndFiltered.get(4));
        assertEquals(of("V10__Patch.sql", asList(10)), sortedAndFiltered.get(5));
    }

    @Test
    public void testVersionFromName(){
        assertEquals(of("V12_22_1_7__test.sql", asList(12,22,1,7)), versionFromName("V12_22_1_7__test.sql"));
        assertEquals(of("V12__test.sql", asList(12)), versionFromName("V12__test.sql"));
        assertEquals(of("V12__test__some_more.sql", asList(12)), versionFromName("V12__test__some_more.sql"));
        assertEquals(of("12__not_valid.sql", asList()), versionFromName("12__not_valid.sql"));
        assertEquals(of("V12_22_not_valid.sql", asList()), versionFromName("V12_22_not_valid.sql"));
        assertEquals(of("V12_22__not_valid.java", asList()), versionFromName("V12_22__not_valid.java"));
    }
}