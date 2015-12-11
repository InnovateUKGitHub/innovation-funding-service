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

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
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

        String expectedPath = fullPathToNewFile.stream().reduce("", (accumulatedPathSoFar, nextPathSegment) -> accumulatedPathSoFar + File.separator+ nextPathSegment);
        assertEquals(expectedPath + File.separator + "thefilename", newFileResult.getPath());
    }
}
