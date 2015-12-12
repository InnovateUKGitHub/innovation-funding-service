package com.worth.ifs.util;

import org.junit.Test;

import static java.io.File.separator;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Tests around FileFunctions methods.
 */
public class FileFunctionsTest {

    @Test
    public void testPathElementsToPathString() {
        assertEquals("path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToPathString(asList("path", "to", "file")));
    }

    @Test
    public void testPathElementsToPathStringWithLeadingSeparator() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToPathString(asList(separator + "path", "to", "file")));
    }

    @Test
    public void testPathElementsToPathStringWithEmptyStringList() {
        assertEquals("", FileFunctions.pathElementsToPathString(asList()));
    }

    @Test
    public void testPathElementsToPathStringWithNullStringList() {
        assertEquals("", FileFunctions.pathElementsToPathString(null));
    }

    @Test
    public void testPathElementsToPathStringWithNullStringElements() {
        assertEquals("path" + separator + "null" + separator + "file",
                FileFunctions.pathElementsToPathString(asList("path", null, "file")));
    }

    @Test
    public void testPathElementsToAbsolutePathString() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToAbsolutePathString(asList("path", "to", "file")));
    }

    @Test
    public void testPathElementsToAbsolutePathStringWithLeadingSeparator() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToAbsolutePathString(asList(separator + "path", "to", "file")));
    }

    @Test
    public void testPathElementsToAbsolutePathStringWithEmptyStringList() {
        assertEquals(separator, FileFunctions.pathElementsToAbsolutePathString(asList()));
    }

    @Test
    public void testPathElementsToAbsolutePathStringWithNullStringList() {
        assertEquals(separator, FileFunctions.pathElementsToAbsolutePathString(null));
    }

    @Test
    public void testPathElementsToAbsolutePathStringWithNullStringElements() {
        assertEquals(separator + "path" + separator + "null" + separator + "file",
                FileFunctions.pathElementsToAbsolutePathString(asList("path", null, "file")));
    }
}
