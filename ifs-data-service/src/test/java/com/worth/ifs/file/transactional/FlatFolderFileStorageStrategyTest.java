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
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleFilterNot;
import static com.worth.ifs.util.FileFunctions.*;
import static java.io.File.separator;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Test the storage strategy of ByFileIdFileStorageStrategy
 */
public class FlatFolderFileStorageStrategyTest {

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
    public void testGetAbsoluteFilePathAndName() {

        FlatFolderFileStorageStrategy strategy = new FlatFolderFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertEquals(tempFolderPathSegmentsWithBaseFolder, strategy.getAbsoluteFilePathAndName(fileEntry).getKey());
        assertEquals("123", strategy.getAbsoluteFilePathAndName(fileEntry).getValue());
    }

    @Test
    public void testGetFullPathToFileUploadFolderWithUnixSeparator() {

        FlatFolderFileStorageStrategy strategy = new FlatFolderFileStorageStrategy("/tmp/path/to/containing/folder", "BaseFolder");

        List<String> fullPathToFileUploadFolder = strategy.getAbsolutePathToFileUploadFolder("/");
        assertEquals(asList("/tmp", "path", "to", "containing", "folder", "BaseFolder"), fullPathToFileUploadFolder);
    }

    @Test
    public void testGetFullPathToFileUploadFolderWithWindowsSeparator() {

        FlatFolderFileStorageStrategy strategy = new FlatFolderFileStorageStrategy("c:\\tmp\\path\\to\\containing\\folder", "BaseFolder");

        List<String> fullPathToFileUploadFolder = strategy.getAbsolutePathToFileUploadFolder("\\");
        assertEquals(asList("c:", "tmp", "path", "to", "containing", "folder", "BaseFolder"), fullPathToFileUploadFolder);
    }

    @Test
    public void testFileExists() throws IOException {

        FlatFolderFileStorageStrategy strategy = new FlatFolderFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
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

        FlatFolderFileStorageStrategy strategy = new FlatFolderFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertFalse(strategy.exists(fileEntry));

        File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);

        try {
            Files.write("Original content", tempFileWithContents, defaultCharset());

            ServiceResult<File> createdFile = strategy.createFile(fileEntry, tempFileWithContents);
            assertTrue(createdFile.isSuccess());
            assertTrue(createdFile.getSuccessObject().exists());

            List<String> expectedPathToFile = combineLists(tempFolderPathSegments, "BaseFolder", "123");
            assertEquals(pathElementsToPath(expectedPathToFile), createdFile.getSuccessObject().toPath());

        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }
    }
}