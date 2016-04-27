package com.worth.ifs.file.transactional;

import com.google.common.io.Files;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleFilterNot;
import static com.worth.ifs.util.FileFunctions.*;
import static java.io.File.separator;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Test common features of any storage strategies
 */
@Ignore("base class")
public abstract class BaseFileStorageStrategyTest {

    protected File tempFolder;
    protected List<String> tempFolderPathSegments;
    protected List<String> tempFolderPathSegmentsWithBaseFolder;
    protected String tempFolderPathAsString;

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
    public void testGetFullPathToFileUploadFolderWithUnixSeparator() {

        BaseFileStorageStrategy strategy = createFileStorageStrategy("/tmp/path/to/containing/folder", "BaseFolder");

        List<String> fullPathToFileUploadFolder = strategy.getAbsolutePathToFileUploadFolder("/");
        assertEquals(asList("/tmp", "path", "to", "containing", "folder", "BaseFolder"), fullPathToFileUploadFolder);
    }

    @Test
    public void testGetFullPathToFileUploadFolderWithWindowsSeparator() {

        BaseFileStorageStrategy strategy = createFileStorageStrategy("c:\\tmp\\path\\to\\containing\\folder", "BaseFolder");

        List<String> fullPathToFileUploadFolder = strategy.getAbsolutePathToFileUploadFolder("\\");
        assertEquals(asList("c:", "tmp", "path", "to", "containing", "folder", "BaseFolder"), fullPathToFileUploadFolder);
    }

    @Test
    public void testFileExists() throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
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

    protected void doTestCreateFile(FileEntry fileEntry, List<String> expectedFilePath) throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        assertFalse(strategy.exists(fileEntry));

        File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);

        try {
            Files.write("Original content", tempFileWithContents, defaultCharset());

            ServiceResult<File> createdFileResult = strategy.createFile(fileEntry, tempFileWithContents);
            assertTrue(createdFileResult.isSuccess());
            File createdFile = createdFileResult.getSuccessObject();

            assertTrue(createdFile.exists());
            assertEquals(pathElementsToPath(expectedFilePath), createdFile.toPath());
            assertEquals("Original content", Files.readFirstLine(createdFile, defaultCharset()));

        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }
    }



    protected void doTestMoveFile(FileEntry fileEntry, List<String> expectedFilePath) throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        assertFalse(strategy.exists(fileEntry));

        File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);

        try {
            Files.write("Original content", tempFileWithContents, defaultCharset());

            ServiceResult<File> movedFileResult = strategy.moveFile(fileEntry.getId(), tempFileWithContents);
            assertTrue(movedFileResult.isSuccess());
            assertTrue(movedFileResult.getSuccessObject().exists());
            File movedFile = movedFileResult.getSuccessObject();
            assertTrue(movedFile.exists());
            assertEquals(pathElementsToPath(expectedFilePath), movedFile.toPath());
            assertEquals("Original content", Files.readFirstLine(movedFile, defaultCharset()));
            assertFalse(tempFileWithContents.exists());
        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }
    }

    @Test
    public void testUpdateFile() throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);

        try {
            Files.write("Original content", tempFileWithContents, defaultCharset());
            ServiceResult<File> createdFile = strategy.createFile(fileEntry, tempFileWithContents);

            Files.write("Updated content", tempFileWithContents, defaultCharset());
            ServiceResult<File> updatedFileResult = strategy.updateFile(fileEntry, tempFileWithContents);
            assertTrue(updatedFileResult.isSuccess());

            File updatedFile = updatedFileResult.getSuccessObject();
            assertEquals(createdFile.getSuccessObject().toPath(), updatedFile.toPath());
            assertEquals("Updated content", Files.readFirstLine(updatedFile, defaultCharset()));
        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }
    }

    protected abstract BaseFileStorageStrategy createFileStorageStrategy(String pathToStorageBase, String containingFolder);
}