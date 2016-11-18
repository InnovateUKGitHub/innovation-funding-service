package com.worth.ifs.util;

import org.junit.Test;

import java.util.List;

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
    public void testPathElementsToAbsolutePathElements() {
        List<String> absolutePath = FileFunctions.pathElementsToAbsolutePathElements(asList("path", "to", "file"), "/");
        assertEquals(asList("/path", "to", "file"), absolutePath);
    }

    @Test
    public void testPathElementsToAbsolutePathElementsButAlreadyAbsolute() {
        List<String> absolutePath = FileFunctions.pathElementsToAbsolutePathElements(asList("/path", "to", "file"), "/");
        assertEquals(asList("/path", "to", "file"), absolutePath);
    }
}
