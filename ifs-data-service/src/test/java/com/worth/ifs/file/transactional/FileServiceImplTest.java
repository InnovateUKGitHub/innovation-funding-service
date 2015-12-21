package com.worth.ifs.file.transactional;

import com.google.common.io.Files;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.domain.builders.FileEntryBuilder;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
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
import static com.worth.ifs.util.FileFunctions.pathElementsToAbsoluteFile;
import static com.worth.ifs.util.FileFunctions.pathElementsToAbsolutePathString;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.isA;
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
        tempFolderPaths = simpleFilterNot(asList(tempFolderPath.getPath().split(File.separator)), StringUtils::isBlank);
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

            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result = service.createFile(fileResource, fakeInputStreamSupplier());

            assertNotNull(result);
            assertTrue(result.isRight());

            File newFileResult = result.getRight().getResult().getKey();

            assertTrue(newFileResult.exists());
            assertEquals("thefilename", newFileResult.getName());

            String expectedPath = pathElementsToAbsolutePathString(fullPathToNewFile);
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

            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result1 = service.createFile(fileResources.get(0), fakeInputStreamSupplier());
            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result2 = service.createFile(fileResources.get(1), fakeInputStreamSupplier());

            assertTrue(result1.isRight());
            assertTrue(result2.isRight());

            File firstFile = result1.getRight().getResult().getKey();
            assertTrue(firstFile.exists());
            assertEquals("thefilename1", firstFile.getName());

            File secondFile = result2.getRight().getResult().getKey();
            assertTrue(secondFile.exists());
            assertEquals("thefilename2", secondFile.getName());

            String expectedPath = pathElementsToAbsolutePathString(fullPathToNewFile);
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

            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result1 = service.createFile(fileResources.get(0), fakeInputStreamSupplier());
            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result2 = service.createFile(fileResources.get(1), fakeInputStreamSupplier());

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

            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result1 = service.createFile(fileResources.get(0), fakeInputStreamSupplier());
            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result2 = service.createFile(fileResources.get(1), fakeInputStreamSupplier());

            assertTrue(result1.isRight());
            assertTrue(result2.isRight());

            File firstFile = result1.getRight().getResult().getKey();
            assertTrue(firstFile.exists());
            assertEquals("samefilename", firstFile.getName());
            String expectedPath1 = pathElementsToAbsolutePathString(fullPathsToNewFiles.get(0));
            assertEquals(expectedPath1 + File.separator + "samefilename", firstFile.getPath());

            File secondFile = result2.getRight().getResult().getKey();
            assertTrue(secondFile.exists());
            assertEquals("samefilename", secondFile.getName());
            String expectedPath2 = pathElementsToAbsolutePathString(fullPathsToNewFiles.get(1));
            assertEquals(expectedPath2 + File.separator + "samefilename", secondFile.getPath());

        } finally {
            FileUtils.deleteDirectory(new File(tempFolderPath, "path"));
        }
    }

    @Test
    public void testCreateFileFailureToCreateFoldersHandledGracefully() {

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
        File tempFolder = pathElementsToAbsoluteFile(tempFolderPaths);
        tempFolder.setReadOnly();

        try {
            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result = service.createFile(fileResource, fakeInputStreamSupplier());
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_CREATE_FOLDERS));
        } finally {
            tempFolder.setWritable(true);
        }
    }

    @Test
    public void testCreateFileFailureToCreateFileHandledGracefully() {

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
        File targetFolder = pathElementsToAbsoluteFile(fullPathToNewFile);
        targetFolder.setReadOnly();

        try {
            Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result = service.createFile(fileResource, fakeInputStreamSupplier());
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
        Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result = service.createFile(file, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_CREATE_FILE));
    }

    @Test
    public void testGetFileByFileEntryId() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;
        List<String> fullPathPlusFilename = combineLists(fullPathToNewFile, asList("thefilename"));
        pathElementsToAbsoluteFile(fullPathPlusFilename).createNewFile();

        try {
            Files.write("Plain text",
                    pathElementsToAbsoluteFile(fullPathPlusFilename), defaultCharset());

            FileEntry existingFileEntry = newFileEntry().with(id(123L)).withFilesizeBytes(10).build();

            when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(existingFileEntry)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

            Either<ServiceFailure, ServiceSuccess<Supplier<InputStream>>> inputStreamResult = service.getFileByFileEntryId(123L);
            assertTrue(inputStreamResult.isRight());

            assertInputStreamContents(inputStreamResult.getRight().getResult().get(), "Plain text");
        } finally {
            pathElementsToAbsoluteFile(fullPathPlusFilename).delete();
        }
    }

    @Test
    public void testGetFileByFileEntryIdButFileEntryEntityDoesntExist() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;
        List<String> fullPathPlusFilename = combineLists(fullPathToNewFile, asList("thefilename"));
        pathElementsToAbsoluteFile(fullPathPlusFilename).createNewFile();

        try {

            Files.write("Plain text",
                    pathElementsToAbsoluteFile(fullPathPlusFilename), defaultCharset());

            when(fileEntryRepository.findOne(123L)).thenReturn(null);

            Either<ServiceFailure, ServiceSuccess<Supplier<InputStream>>> result = service.getFileByFileEntryId(123L);
            assertTrue(result.isLeft());
            assertTrue(result.getLeft().is(UNABLE_TO_FIND_FILE));

        } finally {
            pathElementsToAbsoluteFile(fullPathPlusFilename).delete();
        }
    }

    @Test
    public void testGetFileByFileEntryIdButFileDoesntExist() throws IOException {

        // start by creating a new File to retrieve
        List<String> fullPathToNewFile = tempFolderPaths;

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).withFilesizeBytes(10).build();
        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(existingFileEntry)).thenReturn(Pair.of(fullPathToNewFile, "nonexistent"));

        Either<ServiceFailure, ServiceSuccess<Supplier<InputStream>>> result = service.getFileByFileEntryId(123L);
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

        Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(INCORRECTLY_REPORTED_FILESIZE));
    }

    @Test
    public void testCreateFileWithIncorrectContentType() throws IOException {

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

        Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(INCORRECTLY_REPORTED_MEDIA_TYPE));
    }

    private Supplier<InputStream> fakeInputStreamSupplier() {
        ByteArrayInputStream fakeInputStream = new ByteArrayInputStream("Fake Input Stream".getBytes(defaultCharset()));
        return () -> fakeInputStream;
    }

    private boolean isNotOsx() {
        return !System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private void assumeNotOsx() {
        assumeTrue(isNotOsx());
    }
}
