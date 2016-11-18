package com.worth.ifs.file.transactional;

import com.google.common.io.Files;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.FileFunctions.pathElementsToFile;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test the storage strategy of ByFileIdFileStorageStrategy
 */
public class ByFileIdFileStorageStrategyTest extends BaseFileStorageStrategyTest {

    @Test
    public void testGetFilePathAndName() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        assertEquals(combineLists(tempFolderPathSegmentsWithBaseFolder, "000000000_999999999", "000000_999999", "000_999"), strategy.getFilePathAndName(0L).getKey());
        assertEquals("0", strategy.getFilePathAndName(0L).getValue());
    }

    @Test
    public void testGetAbsoluteFilePathAndName() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertEquals(combineLists(tempFolderPathSegmentsWithBaseFolder, "000000000_999999999", "000000_999999", "000_999"), strategy.getAbsoluteFilePathAndName(fileEntry).getKey());
        assertEquals("123", strategy.getAbsoluteFilePathAndName(fileEntry).getValue());
    }

    @Test
    public void testGetFilePathAndNameForMoreComplexNumber() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        // test a number that is within the middle of the deepest set of partitions
        assertEquals(combineLists(tempFolderPathSegmentsWithBaseFolder, "000000000_999999999", "000000_999999", "5000_5999"), strategy.getFilePathAndName(5123L).getKey());
        assertEquals("5123", strategy.getFilePathAndName(5123L).getValue());

        // test a number that is within the middle of the deepest and next deepest set of partitions
        assertEquals(combineLists(tempFolderPathSegmentsWithBaseFolder, "000000000_999999999", "5000000_5999999", "5123000_5123999"), strategy.getFilePathAndName(5123123L).getKey());
        assertEquals("5123123", strategy.getFilePathAndName(5123123L).getValue());

        // test a number that is within the middle of the deepest, next deepest and least deepest set of partitions
        assertEquals(combineLists(tempFolderPathSegmentsWithBaseFolder, "5000000000_5999999999", "5526000000_5526999999", "5526359000_5526359999"), strategy.getFilePathAndName(5526359849L).getKey());
        assertEquals("5526359849", strategy.getFilePathAndName(5526359849L).getValue());
    }

    @Test
    public void testEachPartitionLevelCanOnlyContainMaximumOf1000Entries() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        List<String> folderPaths = strategy.getFilePathAndName(5526359849L).getKey();
        assertEquals(combineLists(tempFolderPathSegmentsWithBaseFolder, "5000000000_5999999999", "5526000000_5526999999", "5526359000_5526359999"), folderPaths);

        //
        // Check that the parent partitions can only hold a maximum of 1000 child entreis underneath them.
        // Do this by looking at the size of one of the partition's child folders, and then seeing the
        // id range that that child folder has, and then seeing how many of those folder sizes that
        // could be fit underneath it - this'll be based on the current partition's id range of course
        //
        assertEquals("BaseFolder", folderPaths.get(tempFolderPathSegments.size()));
        for (int depth = tempFolderPathSegments.size() + 1; depth < folderPaths.size() - 1; depth++) {

            String[] currentPartitionFromAndToRange = folderPaths.get(depth).split("_");
            long currentPartitionFrom = Long.valueOf(currentPartitionFromAndToRange[0]);
            long currentPartitionTo = Long.valueOf(currentPartitionFromAndToRange[1]);
            long currentPartitionsIdRange = currentPartitionTo - currentPartitionFrom + 1;

            String[] childPartitionFromAndToRange = folderPaths.get(depth + 1).split("_");
            long childPartitionFrom = Long.valueOf(childPartitionFromAndToRange[0]);
            long childPartitionTo = Long.valueOf(childPartitionFromAndToRange[1]);
            long childPartitionsIdRange = childPartitionTo - childPartitionFrom + 1;

            assertEquals(1000, currentPartitionsIdRange / childPartitionsIdRange);
        }
    }

    @Test
    public void testCreateFileWithTwoSameNamesInDifferentFolders() throws IOException {

        FileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        File tempFileWithContents1 = File.createTempFile("tempfilefortesting1", "suffix", tempFolder);
        File tempFileWithContents2 = File.createTempFile("tempfilefortesting2", "suffix", tempFolder);

        FileEntry fileEntry1 = newFileEntry().with(id(1L)).build();
        FileEntry fileEntry2 = newFileEntry().with(id(1001L)).build();

        try {
            Files.write("Original content 1", tempFileWithContents1, defaultCharset());
            Files.write("Original content 2", tempFileWithContents2, defaultCharset());

            ServiceResult<File> createdFile1Result = strategy.createFile(fileEntry1, tempFileWithContents1);
            assertTrue(createdFile1Result.isSuccess());

            ServiceResult<File> createdFile2Result = strategy.createFile(fileEntry2, tempFileWithContents2);
            assertTrue(createdFile2Result.isSuccess());

            File createdFile1 = createdFile1Result.getSuccessObject();
            File createdFile2 = createdFile2Result.getSuccessObject();

            assertTrue(createdFile1.exists());
            assertTrue(createdFile2.exists());
            assertNotEquals(createdFile1.getParent(), createdFile2.getParent());

            assertEquals("Original content 1", Files.readFirstLine(createdFile1, defaultCharset()));
            assertEquals("Original content 2", Files.readFirstLine(createdFile2, defaultCharset()));

        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents1.delete();
            tempFileWithContents2.delete();
        }
    }

    @Test
    public void testCreateFile() throws IOException {
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        doTestCreateFile(fileEntry, combineLists(tempFolderPathSegmentsWithBaseFolder, "000000000_999999999", "000000_999999", "000_999", "123"));
    }

    @Test
    public void testMoveFile() throws IOException {
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        doTestMoveFile(fileEntry, combineLists(tempFolderPathSegmentsWithBaseFolder, "000000000_999999999", "000000_999999", "000_999", "123"));
    }

    @Override
    protected BaseFileStorageStrategy createFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        return new ByFileIdFileStorageStrategy(pathToStorageBase, containingFolder);
    }
}