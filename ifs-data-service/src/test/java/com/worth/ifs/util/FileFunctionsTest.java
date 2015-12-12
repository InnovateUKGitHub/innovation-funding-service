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

    @Test
    public void testPathElementsToPath() {
        assertEquals("path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToPath(asList("path", "to", "file")).toString());
    }

    @Test
    public void testPathElementsToPathWithLeadingSeparator() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToPath(asList(separator + "path", "to", "file")).toString());
    }

    @Test
    public void testPathElementsToPathWithEmptyStringList() {
        assertEquals("", FileFunctions.pathElementsToPath(asList()).toString());
    }

    @Test
    public void testPathElementsToPathWithNullStringList() {
        assertEquals("", FileFunctions.pathElementsToPath(null).toString());
    }

    @Test
    public void testPathElementsToPathWithNullStringElements() {
        assertEquals("path" + separator + "null" + separator + "file",
                FileFunctions.pathElementsToPath(asList("path", null, "file")).toString());
    }


    @Test
    public void testPathElementsToAbsolutePath() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToAbsolutePath(asList("path", "to", "file")).toString());
    }

    @Test
    public void testPathElementsToAbsolutePathWithLeadingSeparator() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToAbsolutePath(asList(separator + "path", "to", "file")).toString());
    }

    @Test
    public void testPathElementsToAbsolutePathWithEmptyStringList() {
        assertEquals(separator, FileFunctions.pathElementsToAbsolutePath(asList()).toString());
    }

    @Test
    public void testPathElementsToAbsolutePathWithNullStringList() {
        assertEquals(separator, FileFunctions.pathElementsToAbsolutePath(null).toString());
    }

    @Test
    public void testPathElementsToAbsolutePathWithNullStringElements() {
        assertEquals(separator + "path" + separator + "null" + separator + "file",
                FileFunctions.pathElementsToAbsolutePath(asList("path", null, "file")).toString());
    }


    @Test
    public void testPathElementsToFile() {
        assertEquals("path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToFile(asList("path", "to", "file")).getPath());
    }

    @Test
    public void testPathElementsToFileWithLeadingSeparator() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToFile(asList(separator + "path", "to", "file")).getPath());
    }

    @Test
    public void testPathElementsToFileWithEmptyStringList() {
        assertEquals("", FileFunctions.pathElementsToFile(asList()).getPath());
    }

    @Test
    public void testPathElementsToFileWithNullStringList() {
        assertEquals("", FileFunctions.pathElementsToFile(null).getPath());
    }

    @Test
    public void testPathElementsToFileWithNullStringElements() {
        assertEquals("path" + separator + "null" + separator + "file",
                FileFunctions.pathElementsToFile(asList("path", null, "file")).getPath());
    }


    @Test
    public void testPathElementsToAbsoluteFile() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToAbsoluteFile(asList("path", "to", "file")).getPath());
    }

    @Test
    public void testPathElementsToAbsoluteFileWithLeadingSeparator() {
        assertEquals(separator + "path" + separator + "to" + separator + "file",
                FileFunctions.pathElementsToAbsoluteFile(asList(separator + "path", "to", "file")).getPath());
    }

    @Test
    public void testPathElementsToAbsoluteFileWithEmptyStringList() {
        assertEquals(separator, FileFunctions.pathElementsToAbsoluteFile(asList()).getPath());
    }

    @Test
    public void testPathElementsToAbsoluteFileWithNullStringList() {
        assertEquals(separator, FileFunctions.pathElementsToAbsoluteFile(null).getPath());
    }

    @Test
    public void testPathElementsToAbsoluteFileWithNullStringElements() {
        assertEquals(separator + "path" + separator + "null" + separator + "file",
                FileFunctions.pathElementsToAbsoluteFile(asList("path", null, "file")).getPath());
    }

}
