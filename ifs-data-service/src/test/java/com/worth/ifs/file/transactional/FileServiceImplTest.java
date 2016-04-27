package com.worth.ifs.file.transactional;

import com.google.common.io.Files;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.domain.builders.FileEntryBuilder;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.FileFunctions.pathElementsToFile;
import static com.worth.ifs.util.FileFunctions.pathElementsToPathString;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@Ignore
public class FileServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FileService service = new FileServiceImpl();

    @Mock
    private FileEntryRepository fileEntryRepository;

    @Mock(name = "temporaryHoldingFileStorageStrategy")
    private FileStorageStrategy temporaryHoldingFileStorageStrategy;

    @Mock(name = "quarantinedFileStorageStrategy")
    private FileStorageStrategy quarantinedFileStorageStrategy;

    @Mock(name = "scannedFileStorageStrategy")
    private FileStorageStrategy scannedFileStorageStrategy;

    @Mock(name = "finalFileStorageStrategy")
    private FileStorageStrategy finalFileStorageStrategy;

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

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");
        List<String> fullPathPlusFilename = combineLists(fullPathToNewFile, "thefilename");

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(temporaryHoldingFileStorageStrategy.createFile(eq(persistedFile), isA(File.class))).thenReturn(serviceSuccess(pathElementsToFile(fullPathPlusFilename)));

        ServiceResult<Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());

        assertNotNull(result);
        assertTrue(result.isSuccess());

        File newFileResult = result.getSuccessObject().getKey();
        assertEquals("thefilename", newFileResult.getName());

        String expectedPath = pathElementsToPathString(fullPathToNewFile);
        assertEquals(expectedPath + File.separator + "thefilename", newFileResult.getPath());
    }

    @Test
    public void testCreateFileFailureToCreateFileOnFilesystemHandledGracefully() {

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(17).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17);

        FileEntry unpersistedFile = fileBuilder.with(id(null)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(temporaryHoldingFileStorageStrategy.createFile(eq(persistedFile), isA(File.class))).thenReturn(serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FOLDERS)));

        ServiceResult<Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(FILES_UNABLE_TO_CREATE_FOLDERS)));
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

        File fileItselfToUpdate = new File("/tmp/path/to/updatedfile");

        try {

            when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
            when(finalFileStorageStrategy.deleteFile(fileToUpdate)).thenReturn(serviceSuccess());
            when(temporaryHoldingFileStorageStrategy.createFile(eq(updatedFile), isA(File.class))).thenReturn(serviceSuccess(fileItselfToUpdate));

            ServiceResult<Pair<File, FileEntry>> result = service.updateFile(updatingFileEntry, fakeInputStreamSupplier("Updated content should be here"));

            assertNotNull(result);
            assertTrue(result.isSuccess());

            File newFileResult = result.getSuccessObject().getKey();
            assertEquals("updatedfile", newFileResult.getName());

            assertEquals("/tmp/path/to/updatedfile", newFileResult.getPath());

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testUpdateFileButNoFileExistsOnFilesystemToUpdate() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");

        try {
            when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
            when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(updatedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            FileEntryResource expectedFileResourceForUpdate = newFileEntryResource().
                    with(id(456L)).
                    withFilesizeBytes(17).
                    build();

            ServiceResult<Pair<File, FileEntry>> result = service.updateFile(expectedFileResourceForUpdate, fakeInputStreamSupplier());

            assertNotNull(result);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(notFoundError(File.class)));

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testUpdateFileWithIncorrectContentLength() throws IOException {

        int incorrectFilesize = 1234;

        FileEntryResource fileResource = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(incorrectFilesize).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().with(id(456L)).withFilesizeBytes(incorrectFilesize);

        FileEntry originalFile = fileBuilder.build();
        FileEntry persistedFile = fileBuilder.build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");

        when(fileEntryRepository.save(originalFile)).thenReturn(persistedFile);
        when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_INCORRECTLY_REPORTED_FILESIZE, 17));
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

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE, "text/plain"));
    }

    @Test
    public void testDeleteFile() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");

        try {

            File existingFileToDelete = pathElementsToFile(combineLists(fullPathToNewFile, "thefilename"));
            Files.createParentDirs(existingFileToDelete);
            existingFileToDelete.createNewFile();
            Files.write("Content to be deleted", existingFileToDelete, defaultCharset());
            assertTrue(existingFileToDelete.exists());

            when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
            when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(fileEntryToDelete)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            ServiceResult<FileEntry> result = service.deleteFile(456L);
            assertNotNull(result);
            assertTrue(result.isSuccess());

            assertFalse(existingFileToDelete.exists());
            verify(fileEntryRepository).delete(fileEntryToDelete);

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testDeleteFileButCantDeleteFileFromFilesystem() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
        when(finalFileStorageStrategy.deleteFile(fileEntryToDelete)).thenReturn(serviceFailure(new Error(FILES_UNABLE_TO_DELETE_FILE)));

        ServiceResult<FileEntry> result = service.deleteFile(456L);
        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(FILES_UNABLE_TO_DELETE_FILE)));

        verify(fileEntryRepository).delete(fileEntryToDelete);
    }

    @Test
    public void testDeleteFileButNoFileExistsOnFilesystemToDelete() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");

        try {

            when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
            when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(fileEntryToDelete)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            ServiceResult<FileEntry> result = service.deleteFile(456L);
            assertNotNull(result);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 456L)));

        } finally {

            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testDeleteFileButNoFileEntryExistsInDatabase() throws IOException {

        when(fileEntryRepository.findOne(456L)).thenReturn(null);

        ServiceResult<FileEntry> result = service.deleteFile(456L);
        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 456L)));
    }

    @Test
    public void testGetFileByFileEntryId() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;
        List<String> fullPathPlusFilename = combineLists(fullPathToNewFile, "thefilename");
        pathElementsToFile(fullPathPlusFilename).createNewFile();

        try {
            Files.write("Plain text",
                    pathElementsToFile(fullPathPlusFilename), defaultCharset());

            FileEntry existingFileEntry = newFileEntry().with(id(123L)).withFilesizeBytes(10).build();

            when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
            when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(existingFileEntry)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            ServiceResult<Supplier<InputStream>> inputStreamResult = service.getFileByFileEntryId(123L);
            assertTrue(inputStreamResult.isSuccess());

            assertInputStreamContents(inputStreamResult.getSuccessObject().get(), "Plain text");
        } finally {
            pathElementsToFile(fullPathPlusFilename).delete();
        }
    }

    @Test
    public void testGetFileByFileEntryIdButFileEntryEntityDoesntExist() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;
        List<String> fullPathPlusFilename = combineLists(fullPathToNewFile, "thefilename");
        pathElementsToFile(fullPathPlusFilename).createNewFile();

        try {

            Files.write("Plain text",
                    pathElementsToFile(fullPathPlusFilename), defaultCharset());

            when(fileEntryRepository.findOne(123L)).thenReturn(null);

            ServiceResult<Supplier<InputStream>> result = service.getFileByFileEntryId(123L);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 123L)));

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
        when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(existingFileEntry)).thenReturn(Pair.of(fullPathToNewFile, "nonexistent"));

        ServiceResult<Supplier<InputStream>> result = service.getFileByFileEntryId(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 123L)));
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

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        ServiceResult<Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_INCORRECTLY_REPORTED_FILESIZE, 17));
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

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, "path", "to", "file");

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(temporaryHoldingFileStorageStrategy.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        ServiceResult<Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE, "text/plain"));
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
