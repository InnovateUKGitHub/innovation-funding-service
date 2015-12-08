package com.worth.ifs.file.service;

import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Test the storage strategy of ByFileIdFileStorageStrategy
 */
public class ByFileIdStorageStrategyTest {

    private File tempFolderPath;

    @Before
    public void setup() {
        tempFolderPath = Files.createTempDir();
    }

    @Test
    public void testGetFilePathAndName() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPath.getPath(), "BaseFolder");

        assertEquals(asList("BaseFolder", "000000000_999999999", "000000_999999", "000_999"), strategy.getFilePathAndName(0L).getLeft());
        assertEquals("0", strategy.getFilePathAndName(0L).getRight());
    }

    @Test
    public void testGetFilePathAndNameForMoreComplexNumber() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPath.getPath(), "BaseFolder");

        // test a number that is within the middle of the deepest set of partitions
        assertEquals(asList("BaseFolder", "000000000_999999999", "000000_999999", "5000_5999"), strategy.getFilePathAndName(5123L).getLeft());
        assertEquals("5123", strategy.getFilePathAndName(5123L).getRight());

        // test a number that is within the middle of the deepest and next deepest set of partitions
        assertEquals(asList("BaseFolder", "000000000_999999999", "5000000_5999999", "5123000_5123999"), strategy.getFilePathAndName(5123123L).getLeft());
        assertEquals("5123123", strategy.getFilePathAndName(5123123L).getRight());

        // test a number that is within the middle of the deepest, next deepest and least deepest set of partitions
        assertEquals(asList("BaseFolder", "5000000000_5999999999", "5526000000_5526999999", "5526359000_5526359999"), strategy.getFilePathAndName(5526359849L).getLeft());
        assertEquals("5526359849", strategy.getFilePathAndName(5526359849L).getRight());
    }
}
