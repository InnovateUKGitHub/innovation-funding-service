package com.worth.ifs.file.transactional;

import com.google.common.io.Files;
import com.worth.ifs.commons.error.Error;
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
import java.nio.file.Path;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.file.builder.FileEntryBuilder.newFileEntry;
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
    public void testMoveFileNoFileToMove() {
        BaseFileStorageStrategy strategy = createFileStorageStrategy("/tmp/path/to/containing/folder", "BaseFolder");
        final ServiceResult<File> fileServiceResult = strategy.moveFile(1L, new File("/does/not/exist"));
        assertTrue(fileServiceResult.isFailure());
        assertTrue(fileServiceResult.getFailure().is(FILES_NO_SUCH_FILE));
    }


    @Test
    public void testMoveFileAlreadyExists() throws IOException {
        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertFalse(strategy.exists(fileEntry));
        File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);
        try {
            // Create a file to try and move over.
            ServiceResult<File> file = strategy.createFile(fileEntry, tempFileWithContents);
            assertTrue(file.isSuccess());
            assertTrue(file.getSuccessObject().exists());
            // Try to move over the existing file.
            final ServiceResult<File> fileServiceResult = strategy.moveFile(fileEntry.getId(), tempFileWithContents);
            assertTrue(fileServiceResult.isFailure());
            assertTrue(fileServiceResult.getFailure().is(FILES_DUPLICATE_FILE_MOVED));
        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }
    }

    @Test
    public void testMoveFileNoFileToMoveAndFileAlreadyExists() throws IOException {
        // This is what we will get if another process has already moved the file.
        // First create a file where we want to move to
        final FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        final FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertFalse(strategy.exists(fileEntry));
        final File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);
        try {
            // Create a file to try and move over.
            ServiceResult<File> file = strategy.createFile(fileEntry, tempFileWithContents);
            assertTrue(file.isSuccess());
            assertTrue(file.getSuccessObject().exists());
            // Try to move over the with the non existing file.
            final ServiceResult<File> fileServiceResult = strategy.moveFile(fileEntry.getId(), new File("/does/not/exist"));
            assertTrue(fileServiceResult.isFailure());
            assertTrue(fileServiceResult.getFailure().is(FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT));
        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }

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

    @Test
    public void testGetFile() throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertFalse(strategy.exists(fileEntry));
        Pair<List<String>, String> absoluteFilePathAndName = strategy.getAbsoluteFilePathAndName(fileEntry);

        try {
            File existingFile = pathElementsToFile(combineLists(absoluteFilePathAndName.getKey(), absoluteFilePathAndName.getValue()));
            Files.createParentDirs(existingFile);
            existingFile.createNewFile();

            ServiceResult<File> getFileResults = strategy.getFile(fileEntry);
            assertTrue(getFileResults.isSuccess());
            assertEquals(existingFile.toPath(), getFileResults.getSuccessObject().toPath());

        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
        }
    }

    @Test
    public void testGetFileButFileDoesntExist() throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertFalse(strategy.exists(fileEntry));

        ServiceResult<File> getFileResults = strategy.getFile(fileEntry);
        assertTrue(getFileResults.isFailure());
        assertTrue(getFileResults.getFailure().is(notFoundError(FileEntry.class, 123L)));
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

    protected void doTestGetAll(List<Pair<FileEntry, Pair<List<String>, String>>> fileEntriesAndExpectedPaths) throws IOException {

        final FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        final File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);

        try {
            // Create the files
            for (final Pair<FileEntry, Pair<List<String>, String>> entry : fileEntriesAndExpectedPaths) {
                ServiceResult<File> createdFileResult = strategy.createFile(entry.getLeft(), tempFileWithContents);
                assertTrue(createdFileResult.isSuccess());
            }
            final List<Pair<List<String>, String>> all = strategy.all();
            assertEquals(fileEntriesAndExpectedPaths.size(), all.size());
            for (final Pair<FileEntry, Pair<List<String>, String>> fileEntryAndExpectedPath : fileEntriesAndExpectedPaths) {
                final Pair<List<String>, String> expectedPath = fileEntryAndExpectedPath.getValue();
                assertTrue(all.contains(expectedPath));
            }
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

    @Test
    public void testUpdateFileButExistingFileDoesntExistOnFilesystem() throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        File tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);

        try {
            Files.write("Updated content", tempFileWithContents, defaultCharset());
            ServiceResult<File> updatedFileResult = strategy.updateFile(fileEntry, tempFileWithContents);
            assertTrue(updatedFileResult.isFailure());
            assertTrue(updatedFileResult.getFailure().is(notFoundError(FileEntry.class, 123L)));
        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents.delete();
        }
    }

    @Test
    public void testCreateFileFailureToCreateFoldersHandledGracefully() {

        BaseFileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "cantcreatethisfolder");

        // make the temp folder readonly so that the subfolder creation fails
        File tempFolder = pathElementsToFile(tempFolderPathSegments);
        tempFolder.setReadOnly();

        try {
            ServiceResult<File> result = strategy.createFile(newFileEntry().build(), new File("dontneedthis"));
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(FILES_UNABLE_TO_CREATE_FOLDERS));
        } finally {
            tempFolder.setWritable(true);
        }
    }

    @Test
    public void testCreateFileFailureToCreateFileHandledGracefully() throws IOException {

        BaseFileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        // make the temp folder readonly so that the subfolder creation fails
        Pair<List<String>, String> targetFolderAndFilename = strategy.getAbsoluteFilePathAndName(newFileEntry().build());
        Path targetFolder = pathElementsToPath(targetFolderAndFilename.getKey());
        java.nio.file.Files.createDirectories(targetFolder);
        targetFolder.toFile().setReadOnly();
        File tempFile = File.createTempFile("tempfile", "suffix", tempFolder);

        try {
            ServiceResult<File> result = strategy.createFile(newFileEntry().build(), tempFile);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(FILES_UNABLE_TO_CREATE_FILE));
        } finally {
            targetFolder.toFile().setWritable(true);
            tempFile.delete();
            FileUtils.deleteDirectory(new File(tempFolder, "BaseFolder"));
        }
    }

    @Test
    public void testCreateFileWithTwoDifferentlyNamedFilesInSameFolder() throws IOException {

        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        File tempFileWithContents1 = File.createTempFile("tempfilefortesting1", "suffix", tempFolder);
        File tempFileWithContents2 = File.createTempFile("tempfilefortesting2", "suffix", tempFolder);

        FileEntry fileEntry1 = newFileEntry().with(id(1L)).build();
        FileEntry fileEntry2 = newFileEntry().with(id(2L)).build();

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
            assertEquals(createdFile1.getParent(), createdFile2.getParent());

            assertEquals("Original content 1", Files.readFirstLine(createdFile1, defaultCharset()));
            assertEquals("Original content 2", Files.readFirstLine(createdFile2, defaultCharset()));

        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents1.delete();
            tempFileWithContents2.delete();
        }
    }

    @Test
    public void testCreateFileWithTwoFilesInSameFolderWithSameNameFailsGracefully() throws IOException {


        FileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        File tempFileWithContents1 = File.createTempFile("tempfilefortesting1", "suffix", tempFolder);
        File tempFileWithContents2 = File.createTempFile("tempfilefortesting2", "suffix", tempFolder);

        FileEntry fileEntry1 = newFileEntry().with(id(1L)).build();
        FileEntry fileEntry2 = newFileEntry().with(id(1L)).build();

        try {
            Files.write("Original content 1", tempFileWithContents1, defaultCharset());
            Files.write("Original content 2", tempFileWithContents2, defaultCharset());

            ServiceResult<File> createdFile1Result = strategy.createFile(fileEntry1, tempFileWithContents1);
            assertTrue(createdFile1Result.isSuccess());

            ServiceResult<File> createdFile2Result = strategy.createFile(fileEntry2, tempFileWithContents2);
            assertTrue(createdFile2Result.isFailure());
            assertTrue(createdFile2Result.getFailure().is(new Error(FILES_DUPLICATE_FILE_CREATED)));

            File createdFile1 = createdFile1Result.getSuccessObject();
            assertTrue(createdFile1.exists());

            assertEquals("Original content 1", Files.readFirstLine(createdFile1, defaultCharset()));

        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(tempFolderPathAsString, "BaseFolder")));
            tempFileWithContents1.delete();
            tempFileWithContents2.delete();
        }
    }

    @Test
    public void testDeleteFile() throws IOException {

        BaseFileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        File tempFile = File.createTempFile("tempfilefortesting1", "suffix", tempFolder);

        FileEntry fileEntry = newFileEntry().with(id(123L)).build();

        try {
            ServiceResult<File> createdFile = strategy.createFile(fileEntry, tempFile);
            assertTrue(createdFile.isSuccess());

            ServiceResult<Void> deletedResult = strategy.deleteFile(fileEntry);
            assertTrue(deletedResult.isSuccess());
            assertFalse(strategy.exists(fileEntry));
        } finally {
            tempFile.delete();
            FileUtils.deleteDirectory(new File(tempFolder, "BaseFolder"));
        }
    }

    @Test
    public void testDeleteFileButNoFileExistsOnFilesystemToDelete() throws IOException {

        BaseFileStorageStrategy strategy = createFileStorageStrategy(tempFolderPathAsString, "BaseFolder");
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();

        try {
            ServiceResult<Void> deletedResult = strategy.deleteFile(fileEntry);
            assertTrue(deletedResult.isFailure());
            assertTrue(deletedResult.getFailure().is(new Error(FILES_UNABLE_TO_DELETE_FILE, FileEntry.class, 123L)));
        } finally {
            FileUtils.deleteDirectory(new File(tempFolder, "BaseFolder"));
        }
    }

    protected abstract BaseFileStorageStrategy createFileStorageStrategy(String pathToStorageBase, String containingFolder);
}