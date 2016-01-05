package com.worth.ifs.file.transactional;

import com.google.common.io.Files;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.domain.builders.FileEntryBuilder;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static com.worth.ifs.InputStreamTestUtil.assertInputStreamContents;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.file.transactional.FileServiceImpl.ServiceFailures.*;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.FileFunctions.pathElementsToFile;
import static com.worth.ifs.util.FileFunctions.pathElementsToPathString;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class FileServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FileService service = new FileServiceImpl();

    @Mock
    private FileEntryRepository fileEntryRepository;

    @Mock
    private FileStorageStrategy fileStorageStrategyMock;

    private File tempFolderPath;
    private List<String> tempFolderPaths;

    @Before
    public void setupTempFolders() {
        tempFolderPath = Files.createTempDir();
        tempFolderPaths = simpleFilterNot(asList(tempFolderPath.getPath().split(File.pathSeparator)), StringUtils::isBlank);
    }

    @After
    public void teardownTempFolder() {
        assertTrue(tempFolderPath.delete());
    }

    @After
    public void teardownInputStream() {
        try {
            fakeInputStreamSupplier().get().close();
        } catch (IOException e) {
            throw new RuntimeException("Could not close input stream", e);
        }
    }

    @Test
    public void testCreateFile() throws IOException {

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(17).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17);

        FileEntry unpersistedFile = fileBuilder.with(id(null)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        try {
            when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            Either<ServiceFailure, Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());

            assertNotNull(result);
            assertTrue(result.isRight());

            File newFileResult = result.getRight().getKey();

            assertTrue(newFileResult.exists());
            assertEquals("thefilename", newFileResult.getName());

            String expectedPath = pathElementsToPathString(fullPathToNewFile);
            assertEquals(expectedPath + File.separator + "thefilename", newFileResult.getPath());

            assertEquals("Fake Input Stream", Files.readFirstLine(newFileResult, defaultCharset()));

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testCreateFileWithTwoDifferentlyNamedFilesInSameFolder() throws IOException {

        List<FileEntryResource> fileResources = newFileEntryResource().
                with(id(null)).
                with(names((i, resource) -> "fileEntry" + i)).
                withFilesizeBytes(17).
                build(2);

        List<FileEntry> persistedFiles = newFileEntry().
                with(incrementingIds()).
                with(names((i, resource) -> "fileEntry" + i)).
                withFilesizeBytes(17).
                build(2);

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        try {

            forEachWithIndex(fileResources, (i, resource) -> {
                FileEntry unpersistedFile = new FileEntry(resource.getId(), resource.getName(), resource.getMediaType(), resource.getFilesizeBytes());
                when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFiles.get(i));
            });

            forEachWithIndex(persistedFiles, (i, file) -> {
                when(fileStorageStrategyMock.getAbsoluteFilePathAndName(file)).thenReturn(Pair.of(fullPathToNewFile, "thefilename" + (i + 1)));
            });

            Either<ServiceFailure, Pair<File, FileEntry>> result1 = service.createFile(fileResources.get(0), fakeInputStreamSupplier());
            Either<ServiceFailure, Pair<File, FileEntry>> result2 = service.createFile(fileResources.get(1), fakeInputStreamSupplier());

            assertTrue(result1.isRight());
            assertTrue(result2.isRight());

            File firstFile = result1.getRight().getKey();
            assertTrue(firstFile.exists());
            assertEquals("thefilename1", firstFile.getName());

            File secondFile = result2.getRight().getKey();
            assertTrue(secondFile.exists());
            assertEquals("thefilename2", secondFile.getName());

            String expectedPath = pathElementsToPathString(fullPathToNewFile);
            assertEquals(expectedPath + File.separator + "thefilename1", firstFile.getPath());
            assertEquals(expectedPath + File.separator + "thefilename2", secondFile.getPath());

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testCreateFileWithTwoFilesInSameFolderWithSameNameFailsGracefully() throws IOException {

        List<FileEntryResource> fileResources = newFileEntryResource().
                with(id(null)).
                with(names((i, resource) -> "fileEntry" + i)).
                withFilesizeBytes(17).
                build(2);

        List<FileEntry> persistedFiles = newFileEntry().
                with(incrementingIds()).
                with(names((i, resource) -> "fileEntry" + i)).
                withFilesizeBytes(17).
                build(2);

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        try {

            forEachWithIndex(fileResources, (i, resource) -> {
                FileEntry unpersistedFile = new FileEntry(resource.getId(), resource.getName(), resource.getMediaType(), resource.getFilesizeBytes());
                when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFiles.get(i));
            });

            persistedFiles.forEach(file -> {
                when(fileStorageStrategyMock.getAbsoluteFilePathAndName(file)).thenReturn(Pair.of(fullPathToNewFile, "samefilename"));
            });

            Either<ServiceFailure, Pair<File, FileEntry>> result1 = service.createFile(fileResources.get(0), fakeInputStreamSupplier());
            Either<ServiceFailure, Pair<File, FileEntry>> result2 = service.createFile(fileResources.get(1), fakeInputStreamSupplier());

            assertTrue(result1.isRight());
            assertTrue(result2.isLeft());
            assertTrue(result2.getLeft().is(DUPLICATE_FILE_CREATED));

        } finally {
            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testCreateFileWithTwoSameNamesInDifferentFolders() throws IOException {

        List<FileEntryResource> fileResources = newFileEntryResource().
                with(id(null)).
                with(names((i, resource) -> "fileEntry" + i)).
                withFilesizeBytes(17).
                build(2);

        List<FileEntry> persistedFiles = newFileEntry().
                with(incrementingIds()).
                with(names((i, resource) -> "fileEntry" + i)).
                withFilesizeBytes(17).
                build(2);

        List<List<String>> fullPathsToNewFiles = asList(
                combineLists(tempFolderPaths, asList("path", "to", "file")),
                combineLists(tempFolderPaths, asList("path", "to2", "file"))
                );

        try {
            forEachWithIndex(fileResources, (i, resource) -> {
                FileEntry unpersistedFile = new FileEntry(resource.getId(), resource.getName(), resource.getMediaType(), resource.getFilesizeBytes());
                when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFiles.get(i));
            });

            forEachWithIndex(persistedFiles, (i, file) -> {
                when(fileStorageStrategyMock.getAbsoluteFilePathAndName(file)).thenReturn(Pair.of(fullPathsToNewFiles.get(i), "samefilename"));
            });

            Either<ServiceFailure, Pair<File, FileEntry>> result1 = service.createFile(fileResources.get(0), fakeInputStreamSupplier());
            Either<ServiceFailure, Pair<File, FileEntry>> result2 = service.createFile(fileResources.get(1), fakeInputStreamSupplier());

            assertTrue(result1.isRight());
            assertTrue(result2.isRight());

            File firstFile = result1.getRight().getKey();
            assertTrue(firstFile.exists());
            assertEquals("samefilename", firstFile.getName());
            String expectedPath1 = pathElementsToPathString(fullPathsToNewFiles.get(0));
            assertEquals(expectedPath1 + File.separator + "samefilename", firstFile.getPath());

            File secondFile = result2.getRight().getKey();
            assertTrue(secondFile.exists());
            assertEquals("samefilename", secondFile.getName());
            String expectedPath2 = pathElementsToPathString(fullPathsToNewFiles.get(1));
            assertEquals(expectedPath2 + File.separator + "samefilename", secondFile.getPath());

        } finally {
            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testCreateFileFailureToCreateFoldersHandledGracefully() {

        assumeNotWindows();

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(17).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17);

        FileEntry unpersistedFile = fileBuilder.with(id(null)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();
        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("cantcreatethisfolder"));

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        // make the temp folder readonly so that the subfolder creation fails
        File tempFolder = pathElementsToFile(tempFolderPaths);
        tempFolder.setReadOnly();

        try {
            Either<ServiceFailure, Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_CREATE_FOLDERS));
        } finally {
            tempFolder.setWritable(true);
        }
    }

    @Test
    public void testCreateFileFailureToCreateFileHandledGracefully() {

        assumeNotWindows();

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(17).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17);

        FileEntry unpersistedFile = fileBuilder.with(id(null)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();
        List<String> fullPathToNewFile = tempFolderPaths;

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        // make the target folder readonly so that the subfolder creation fails
        File targetFolder = pathElementsToFile(fullPathToNewFile);
        targetFolder.setReadOnly();

        try {
            Either<ServiceFailure, Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_CREATE_FILE));
        } finally {
            targetFolder.setWritable(true);
        }
    }

    @Test
    public void testCreateFileWithUnexpectedExceptionsHandlesFailureGracefully() {

        RuntimeException exception = new RuntimeException("you shall not pass!");
        when(fileEntryRepository.save(isA(FileEntry.class))).thenThrow(exception);

        FileEntryResource file = newFileEntryResource().with(id(null)).withFilesizeBytes(17).build();
        Either<ServiceFailure, Pair<File, FileEntry>> result = service.createFile(file, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_CREATE_FILE));
    }

    @Test
    public void testUpdateFile() throws IOException {

        FileEntryResource updatingFileEntry = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(30).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        try {

            File existingFileToUpdate = pathElementsToFile(combineLists(fullPathToNewFile, asList("thefilename")));
            Files.createParentDirs(existingFileToUpdate);
            existingFileToUpdate.createNewFile();

            Files.write("Original content", existingFileToUpdate, defaultCharset());

            when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(updatedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            Either<ServiceFailure, Pair<File, FileEntry>> result = service.updateFile(updatingFileEntry, fakeInputStreamSupplier("Updated content should be here"));

            assertNotNull(result);
            assertTrue(result.isRight());

            File newFileResult = result.getRight().getKey();

            assertTrue(newFileResult.exists());
            assertEquals("thefilename", newFileResult.getName());

            String expectedPath = pathElementsToPathString(fullPathToNewFile);
            assertEquals(expectedPath + File.separator + "thefilename", newFileResult.getPath());

            assertEquals("Updated content should be here", Files.readFirstLine(newFileResult, defaultCharset()));

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testUpdateFileButNoFileExistsOnFilesystemToUpdate() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        try {
            when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(updatedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            FileEntryResource expectedFileResourceForUpdate = newFileEntryResource().
                    with(id(456L)).
                    withFilesizeBytes(17).
                    build();

            Either<ServiceFailure, Pair<File, FileEntry>> result = service.updateFile(expectedFileResourceForUpdate, fakeInputStreamSupplier());

            assertNotNull(result);
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_FIND_FILE));

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testUpdateFileWithIncorrectContentLength() throws IOException {

        int incorrectFilesize = 1234;

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(incorrectFilesize).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(incorrectFilesize);

        FileEntry unpersistedFile = fileBuilder.with(id(456L)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        Either<ServiceFailure, Pair<File, FileEntry>> result = service.updateFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(INCORRECTLY_REPORTED_FILESIZE));
    }

    @Test
    public void testUpdateFileWithIncorrectContentType() throws IOException {

        assumeNotWindows();
        assumeNotOsx();

        FileEntryResource fileResource = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(17).
                withMediaType("application/pdf").
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17).withMediaType("application/pdf");

        FileEntry unpersistedFile = fileBuilder.with(id(456L)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        Either<ServiceFailure, Pair<File, FileEntry>> result = service.updateFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(INCORRECTLY_REPORTED_MEDIA_TYPE));
    }

    @Test
    public void testDeleteFile() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        try {

            File existingFileToDelete = pathElementsToFile(combineLists(fullPathToNewFile, asList("thefilename")));
            Files.createParentDirs(existingFileToDelete);
            existingFileToDelete.createNewFile();
            Files.write("Content to be deleted", existingFileToDelete, defaultCharset());
            assertTrue(existingFileToDelete.exists());

            when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(fileEntryToDelete)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            Either<ServiceFailure, FileEntry> result = service.deleteFile(456L);
            assertNotNull(result);
            assertTrue(result.isRight());

            assertFalse(existingFileToDelete.exists());
            verify(fileEntryRepository).delete(fileEntryToDelete);

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testDeleteFileButCantDeleteFile() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        File existingFileToDelete = pathElementsToFile(combineLists(fullPathToNewFile, asList("thefilename", "andachildthatwillstopdeletion")));

        try {

            Files.createParentDirs(existingFileToDelete);
            existingFileToDelete.createNewFile();
            Files.write("Content to be deleted", existingFileToDelete, defaultCharset());

            when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(fileEntryToDelete)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            Either<ServiceFailure, FileEntry> result = service.deleteFile(456L);
            assertNotNull(result);
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_DELETE_FILE));

            assertTrue(existingFileToDelete.exists());
            verify(fileEntryRepository).delete(fileEntryToDelete);

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testDeleteFileButNoFileExistsOnFilesystemToDelete() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        try {

            when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(fileEntryToDelete)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            Either<ServiceFailure, FileEntry> result = service.deleteFile(456L);
            assertNotNull(result);
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_FIND_FILE));

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testDeleteFileButNoFileEntryExistsInDatabase() throws IOException {

        when(fileEntryRepository.findOne(456L)).thenReturn(null);

        Either<ServiceFailure, FileEntry> result = service.deleteFile(456L);
        assertNotNull(result);
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_FIND_FILE));
    }

    @Test
    public void testGetFileByFileEntryId() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;
        List<String> fullPathPlusFilename = combineLists(fullPathToNewFile, asList("thefilename"));
        pathElementsToFile(fullPathPlusFilename).createNewFile();

        try {
            Files.write("Plain text",
                    pathElementsToFile(fullPathPlusFilename), defaultCharset());

            FileEntry existingFileEntry = newFileEntry().with(id(123L)).withFilesizeBytes(10).build();

            when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(existingFileEntry)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            Either<ServiceFailure, Supplier<InputStream>> inputStreamResult = service.getFileByFileEntryId(123L);
            assertTrue(inputStreamResult.isRight());

            assertInputStreamContents(inputStreamResult.getRight().get(), "Plain text");
        } finally {
            pathElementsToFile(fullPathPlusFilename).delete();
        }
    }

    @Test
    public void testGetFileByFileEntryIdButFileEntryEntityDoesntExist() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;
        List<String> fullPathPlusFilename = combineLists(fullPathToNewFile, asList("thefilename"));
        pathElementsToFile(fullPathPlusFilename).createNewFile();

        try {

            Files.write("Plain text",
                    pathElementsToFile(fullPathPlusFilename), defaultCharset());

            when(fileEntryRepository.findOne(123L)).thenReturn(null);

            Either<ServiceFailure, Supplier<InputStream>> result = service.getFileByFileEntryId(123L);
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_FIND_FILE));

        } finally {
            pathElementsToFile(fullPathPlusFilename).delete();
        }
    }

    @Test
    public void testGetFileByFileEntryIdButFileDoesntExist() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).withFilesizeBytes(10).build();
        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(existingFileEntry)).thenReturn(Pair.of(fullPathToNewFile, "nonexistent"));

        Either<ServiceFailure, Supplier<InputStream>> result = service.getFileByFileEntryId(123L);
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_FIND_FILE));
    }

    @Test
    public void testCreateFileWithIncorrectContentLength() throws IOException {

        int incorrectFilesize = 1234;

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(incorrectFilesize).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(incorrectFilesize);

        FileEntry unpersistedFile = fileBuilder.with(id(null)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        Either<ServiceFailure, Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(INCORRECTLY_REPORTED_FILESIZE));
    }

    @Test
    public void testCreateFileWithIncorrectContentType() throws IOException {

        assumeNotWindows();
        assumeNotOsx();

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(17).
                withMediaType("application/pdf").
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17).withMediaType("application/pdf");

        FileEntry unpersistedFile = fileBuilder.with(id(null)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        Either<ServiceFailure, Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(INCORRECTLY_REPORTED_MEDIA_TYPE));
    }

    private Supplier<InputStream> fakeInputStreamSupplier() {
        return fakeInputStreamSupplier("Fake Input Stream");
    }

    private Supplier<InputStream> fakeInputStreamSupplier(String content) {
        ByteArrayInputStream fakeInputStream = new ByteArrayInputStream(content.getBytes(defaultCharset()));
        return () -> fakeInputStream;
    }

    private boolean isNotOsx() {
        return !System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private boolean isNotWindows() {
        return !System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private void assumeNotWindows() {
        assumeTrue(isNotWindows());
    }

    private void assumeNotOsx() {
        assumeTrue(isNotOsx());
    }
}
