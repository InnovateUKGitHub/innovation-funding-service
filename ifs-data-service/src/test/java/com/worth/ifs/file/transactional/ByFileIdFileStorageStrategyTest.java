package com.worth.ifs.file.transactional;

import com.google.common.io.Files;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.FileFunctions.*;
import static java.io.File.separator;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Test the storage strategy of ByFileIdFileStorageStrategy
 */
public class ByFileIdFileStorageStrategyTest {

    private File tempFolder;
    private List<String> tempFolderPathSegments;
    private List<String> tempFolderPathSegmentsWithBaseFolder;
    private String tempFolderPathAsString;

    @Before
    public void setupTempFolders() {
        tempFolder = Files.createTempDir();
        tempFolderPathSegments = pathElementsToAbsolutePathElements(simpleFilterNot(asList(tempFolder.getPath().split(separator)), StringUtils::isBlank), separator);
        tempFolderPathSegmentsWithBaseFolder = combineLists(tempFolderPathSegments, "BaseFolder");
        tempFolderPathAsString = pathElementsToAbsolutePathString(tempFolderPathSegments, separator);
    }

    @After
    public void teardownTempFolder() {
        assertTrue(tempFolder.delete());
    }

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
    public void testGetFullPathToFileUploadFolderWithUnixSeparator() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy("/tmp/path/to/containing/folder", "BaseFolder");

        List<String> fullPathToFileUploadFolder = strategy.getAbsolutePathToFileUploadFolder("/");
        assertEquals(asList("/tmp", "path", "to", "containing", "folder", "BaseFolder"), fullPathToFileUploadFolder);
    }

    @Test
    public void testGetFullPathToFileUploadFolderWithWindowsSeparator() {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy("c:\\tmp\\path\\to\\containing\\folder", "BaseFolder");

        List<String> fullPathToFileUploadFolder = strategy.getAbsolutePathToFileUploadFolder("\\");
        assertEquals(asList("c:", "tmp", "path", "to", "containing", "folder", "BaseFolder"), fullPathToFileUploadFolder);
    }

    @Test
    public void testFileExists() throws IOException {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertFalse(strategy.exists(fileEntry));
        Pair<List<String>, String> absoluteFilePathAndName = strategy.getAbsoluteFilePathAndName(fileEntry);

        try {
            File newFileWithNonMatchingFilename = pathElementsToFile(combineLists(absoluteFilePathAndName.getKey(), "122"));
            Files.createParentDirs(newFileWithNonMatchingFilename);
            newFileWithNonMatchingFilename.createNewFile();
            assertFalse(strategy.exists(fileEntry));

            File matchingFile = pathElementsToFile(combineLists(absoluteFilePathAndName.getKey(), absoluteFilePathAndName.getValue()));
            matchingFile.createNewFile();
            assertTrue(strategy.exists(fileEntry));
        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
        }
    }

    @Test
    public void testCreateFile() throws IOException {

        ByFileIdFileStorageStrategy strategy = new ByFileIdFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertFalse(strategy.exists(fileEntry));

        File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);

        try {
            Files.write("Original content", tempFileWithContents, defaultCharset());

            ServiceResult<File> createdFile = strategy.createFile(fileEntry, tempFileWithContents);
            assertTrue(createdFile.isSuccess());
            assertTrue(createdFile.getSuccessObject().exists());

            List<String> expectedPathToFile = combineLists(tempFolderPathSegments, "BaseFolder", "000000000_999999999", "000000_999999", "000_999", "123");
            assertEquals(pathElementsToPath(expectedPathToFile), createdFile.getSuccessObject().toPath());

        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }
    }
}