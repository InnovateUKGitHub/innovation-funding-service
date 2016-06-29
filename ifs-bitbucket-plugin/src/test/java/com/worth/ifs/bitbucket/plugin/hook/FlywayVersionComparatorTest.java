package com.worth.ifs.bitbucket.plugin.hook;

import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class FlywayVersionComparatorTest
{
    @Test
    public void testCompareTo(){
        final FlywayVersionComparator comparator = new FlywayVersionComparator();
        assertEquals(0, comparator.compare(new ArrayList<>(), new ArrayList<>()));
        assertEquals(1, comparator.compare(asList(10), asList(1)));
        assertEquals(-1, comparator.compare(asList(1), asList(10)));
        assertEquals(1, comparator.compare(asList(1, 1), asList(1)));
        assertEquals(-1, comparator.compare(asList(1), asList(1, 1)));
        assertEquals(1, comparator.compare(asList(1, 2), asList(1, 1)));
        assertEquals(0, comparator.compare(asList(1, 2), asList(1, 2)));
        assertEquals(1, comparator.compare(asList(1, 2, 3, 4, 6), asList(1, 2, 3, 4, 5)));
    }
}