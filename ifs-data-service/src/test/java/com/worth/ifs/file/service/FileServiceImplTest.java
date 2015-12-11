package com.worth.ifs.file.service;

import com.google.common.io.Files;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.domain.builders.FileEntryBuilder;
import com.worth.ifs.file.repository.FileEntryRepository;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.file.service.FileServiceImpl.ServiceFailures.DUPLICATE_FILE_CREATED;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.forEachWithIndex;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
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

    private List<String> tempFolderPaths;

    @Before
    public void setup() {
        File tempFolderPath = Files.createTempDir();
        tempFolderPaths = asList(tempFolderPath.getPath().substring(1).split(File.separator));
    }

    @Test
    public void testCreateFile() {

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                build();

        FileEntryBuilder fileBuilder = newFileEntry();

        FileEntry unpersistedFile = fileBuilder.with(id(null)).build();
        FileEntry persistedFile = fileBuilder.with(id(456L)).build();

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFile);
        when(fileStorageStrategyMock.getAbsoluteFilePathAndName(persistedFile)).thenReturn(Pair.of(fullPathToNewFile, "thefilename"));

        Either<ServiceFailure, ServiceSuccess<File>> result = service.createFile(fileResource);

        assertNotNull(result);
        assertTrue(result.isRight());

        File newFileResult = result.getRight().getResult();

        assertTrue(newFileResult.exists());
        assertEquals("thefilename", newFileResult.getName());

        String expectedPath = fullPathToNewFile.stream().reduce("", (accumulatedPathSoFar, nextPathSegment) -> accumulatedPathSoFar + File.separator + nextPathSegment);
        assertEquals(expectedPath + File.separator + "thefilename", newFileResult.getPath());
    }

    @Test
    public void testCreateFileWithTwoDifferentlyNamedFilesInSameFolder() {

        List<FileEntryResource> fileResources = newFileEntryResource().
                with(id(null)).
                with(names((i, resource) -> "fileEntry" + i)).
                build(2);

        List<FileEntry> persistedFiles = newFileEntry().
                with(incrementingIds()).
                with(names((i, resource) -> "fileEntry" + i)).
                build(2);

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        forEachWithIndex(fileResources, (i, resource) -> {
            FileEntry unpersistedFile = new FileEntry(resource.getId(), resource.getName(), resource.getMimeType(), resource.getFilesizeBytes());
            when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFiles.get(i));
        });

        forEachWithIndex(persistedFiles, (i, file) -> {
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(file)).thenReturn(Pair.of(fullPathToNewFile, "thefilename" + (i + 1)));
        });


        Either<ServiceFailure, ServiceSuccess<File>> result1 = service.createFile(fileResources.get(0));
        Either<ServiceFailure, ServiceSuccess<File>> result2 = service.createFile(fileResources.get(1));

        assertTrue(result1.isRight());
        assertTrue(result2.isRight());

        File firstFile = result1.getRight().getResult();
        assertTrue(firstFile.exists());
        assertEquals("thefilename1", firstFile.getName());

        File secondFile = result2.getRight().getResult();
        assertTrue(secondFile.exists());
        assertEquals("thefilename2", secondFile.getName());

        String expectedPath = fullPathToNewFile.stream().reduce("", (accumulatedPathSoFar, nextPathSegment) -> accumulatedPathSoFar + File.separator + nextPathSegment);
        assertEquals(expectedPath + File.separator + "thefilename1", firstFile.getPath());
        assertEquals(expectedPath + File.separator + "thefilename2", secondFile.getPath());
    }

    @Test
    public void testCreateFileWithTwoFilesInSameFolderWithSameNameFailsGracefully() {

        List<FileEntryResource> fileResources =
                newFileEntryResource().with(id(null)).with(names((i, resource) -> "fileEntry" + i)).build(2);

        List<FileEntry> persistedFiles =
                newFileEntry().with(incrementingIds()).with(names((i, resource) -> "fileEntry" + i)).build(2);

        List<String> fullPathToNewFile = combineLists(tempFolderPaths, asList("path", "to", "file"));

        forEachWithIndex(fileResources, (i, resource) -> {
            FileEntry unpersistedFile = new FileEntry(resource.getId(), resource.getName(), resource.getMimeType(), resource.getFilesizeBytes());
            when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFiles.get(i));
        });

        persistedFiles.forEach(file -> {
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(file)).thenReturn(Pair.of(fullPathToNewFile, "samefilename"));
        });

        Either<ServiceFailure, ServiceSuccess<File>> result1 = service.createFile(fileResources.get(0));
        Either<ServiceFailure, ServiceSuccess<File>> result2 = service.createFile(fileResources.get(1));

        assertTrue(result1.isRight());
        assertTrue(result2.isLeft());
        assertTrue(result2.getLeft().is(DUPLICATE_FILE_CREATED));
    }

    @Test
    public void testCreateFileWithTwoSameNamesInDifferentFolders() {

        List<FileEntryResource> fileResources = newFileEntryResource().
                with(id(null)).
                with(names((i, resource) -> "fileEntry" + i)).
                build(2);

        List<FileEntry> persistedFiles = newFileEntry().
                with(incrementingIds()).
                with(names((i, resource) -> "fileEntry" + i)).
                build(2);

        List<List<String>> fullPathsToNewFiles = asList(
                combineLists(tempFolderPaths, asList("path", "to", "file")),
                combineLists(tempFolderPaths, asList("path", "to2", "file"))
                );

        forEachWithIndex(fileResources, (i, resource) -> {
            FileEntry unpersistedFile = new FileEntry(resource.getId(), resource.getName(), resource.getMimeType(), resource.getFilesizeBytes());
            when(fileEntryRepository.save(unpersistedFile)).thenReturn(persistedFiles.get(i));
        });

        forEachWithIndex(persistedFiles, (i, file) -> {
            when(fileStorageStrategyMock.getAbsoluteFilePathAndName(file)).thenReturn(Pair.of(fullPathsToNewFiles.get(i), "samefilename"));
        });


        Either<ServiceFailure, ServiceSuccess<File>> result1 = service.createFile(fileResources.get(0));
        Either<ServiceFailure, ServiceSuccess<File>> result2 = service.createFile(fileResources.get(1));

        assertTrue(result1.isRight());
        assertTrue(result2.isRight());

        File firstFile = result1.getRight().getResult();
        assertTrue(firstFile.exists());
        assertEquals("samefilename", firstFile.getName());
        String expectedPath1 = fullPathsToNewFiles.get(0).stream().reduce("", (accumulatedPathSoFar, nextPathSegment) -> accumulatedPathSoFar + File.separator + nextPathSegment);
        assertEquals(expectedPath1 + File.separator + "samefilename", firstFile.getPath());

        File secondFile = result2.getRight().getResult();
        assertTrue(secondFile.exists());
        assertEquals("samefilename", secondFile.getName());
        String expectedPath2 = fullPathsToNewFiles.get(1).stream().reduce("", (accumulatedPathSoFar, nextPathSegment) -> accumulatedPathSoFar + File.separator + nextPathSegment);
        assertEquals(expectedPath2 + File.separator + "samefilename", secondFile.getPath());
    }
}
