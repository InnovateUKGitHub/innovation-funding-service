package org.innovateuk.ifs.file.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.RootUnitTestMocksTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.builder.FileEntryBuilder;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static java.nio.charset.Charset.defaultCharset;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.*;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.FileFunctions.pathElementsToFile;
import static org.innovateuk.ifs.util.FileFunctions.pathElementsToPathString;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 *
 */
public class FileServiceImplTest extends RootUnitTestMocksTest {

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

    @After
    public void verifyMockExpectations() {
        verifyNoMoreFileServiceInteractions();
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

        List<String> fullPathToNewFile = combineLists("path", "to", "file");
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

        verify(fileEntryRepository).save(unpersistedFile);
        verify(temporaryHoldingFileStorageStrategy).createFile(eq(persistedFile), isA(File.class));
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

        verify(fileEntryRepository).save(unpersistedFile);
        verify(temporaryHoldingFileStorageStrategy).createFile(eq(persistedFile), isA(File.class));
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

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);
        when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
        when(temporaryHoldingFileStorageStrategy.exists(updatedFile)).thenReturn(false);
        when(finalFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceSuccess(fileItselfToUpdate));
        when(finalFileStorageStrategy.deleteFile(updatedFile)).thenReturn(serviceSuccess());
        when(temporaryHoldingFileStorageStrategy.createFile(eq(updatedFile), isA(File.class))).thenReturn(serviceSuccess(fileItselfToUpdate));

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(updatingFileEntry, fakeInputStreamSupplier("Updated content should be here"));

        assertNotNull(result);
        assertTrue(result.isSuccess());

        File newFileResult = result.getSuccessObject().getKey();
        assertEquals("updatedfile", newFileResult.getName());

        assertEquals("/tmp/path/to/updatedfile", newFileResult.getPath());

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).save(fileToUpdate);
        verify(temporaryHoldingFileStorageStrategy).exists(updatedFile);
        verify(finalFileStorageStrategy).getFile(updatedFile);
        verify(finalFileStorageStrategy).deleteFile(updatedFile);
        verify(temporaryHoldingFileStorageStrategy).createFile(eq(updatedFile), isA(File.class));
    }

    @Test
    public void testUpdateFileDoesntDeleteOriginalFileUntilNewFileSuccessfullyCreated() throws IOException {

        FileEntryResource updatingFileEntry = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(30).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFile = fileBuilder.with(id(456L)).build();

        File fileItselfToUpdate = new File("/tmp/path/to/updatedfile");

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);
        when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
        when(temporaryHoldingFileStorageStrategy.exists(updatedFile)).thenReturn(false);
        when(finalFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceSuccess(fileItselfToUpdate));
        when(temporaryHoldingFileStorageStrategy.createFile(eq(updatedFile), isA(File.class))).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(updatingFileEntry, fakeInputStreamSupplier("Updated content should be here"));

        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).save(fileToUpdate);
        verify(temporaryHoldingFileStorageStrategy).exists(updatedFile);
        verify(finalFileStorageStrategy).getFile(updatedFile);
        verify(temporaryHoldingFileStorageStrategy).createFile(eq(updatedFile), isA(File.class));
    }

    @Test
    public void testUpdateFileAndOriginalFileIsCurrentlyAwaitingScanning() throws IOException {

        FileEntryResource updatingFileEntry = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(30).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFile = fileBuilder.with(id(456L)).build();

        File fileItselfToUpdate = new File("/tmp/path/to/updatedfile");

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);
        when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
        when(temporaryHoldingFileStorageStrategy.exists(updatedFile)).thenReturn(true);
        when(temporaryHoldingFileStorageStrategy.createFile(eq(updatedFile), isA(File.class))).thenReturn(serviceSuccess(fileItselfToUpdate));
        when(temporaryHoldingFileStorageStrategy.deleteFile(updatedFile)).thenReturn(serviceSuccess());

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(updatingFileEntry, fakeInputStreamSupplier("Updated content should be here"));

        assertNotNull(result);
        assertTrue(result.isSuccess());

        File newFileResult = result.getSuccessObject().getKey();
        assertEquals("updatedfile", newFileResult.getName());

        assertEquals("/tmp/path/to/updatedfile", newFileResult.getPath());

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).save(fileToUpdate);
        verify(temporaryHoldingFileStorageStrategy).exists(updatedFile);
        verify(temporaryHoldingFileStorageStrategy).createFile(eq(updatedFile), isA(File.class));
        verify(temporaryHoldingFileStorageStrategy).deleteFile(updatedFile);
    }

    @Test
    public void testUpdateFileAndOriginalFileIsCurrentlyQuarantined() throws IOException {

        FileEntryResource updatingFileEntry = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(30).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFile = fileBuilder.with(id(456L)).build();

        File fileItselfToUpdate = new File("/tmp/path/to/updatedfile");

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);
        when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
        when(temporaryHoldingFileStorageStrategy.exists(updatedFile)).thenReturn(false);

        when(finalFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(temporaryHoldingFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(scannedFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(quarantinedFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceSuccess(fileItselfToUpdate));

        when(temporaryHoldingFileStorageStrategy.createFile(eq(updatedFile), isA(File.class))).thenReturn(serviceSuccess(fileItselfToUpdate));
        when(quarantinedFileStorageStrategy.deleteFile(updatedFile)).thenReturn(serviceSuccess());

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(updatingFileEntry, fakeInputStreamSupplier("Updated content should be here"));

        assertNotNull(result);
        assertTrue(result.isSuccess());

        File newFileResult = result.getSuccessObject().getKey();
        assertEquals("updatedfile", newFileResult.getName());

        assertEquals("/tmp/path/to/updatedfile", newFileResult.getPath());

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).save(fileToUpdate);
        verify(temporaryHoldingFileStorageStrategy).exists(updatedFile);
        verify(finalFileStorageStrategy).getFile(updatedFile);
        verify(temporaryHoldingFileStorageStrategy).getFile(updatedFile);
        verify(scannedFileStorageStrategy).getFile(updatedFile);
        verify(quarantinedFileStorageStrategy).getFile(updatedFile);
        verify(temporaryHoldingFileStorageStrategy).createFile(eq(updatedFile), isA(File.class));
        verify(quarantinedFileStorageStrategy).deleteFile(updatedFile);
    }

    @Test
    public void testUpdateFileAndOriginalFileIsCurrentlyScanned() throws IOException {

        FileEntryResource updatingFileEntry = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(30).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFile = fileBuilder.with(id(456L)).build();

        File fileItselfToUpdate = new File("/tmp/path/to/updatedfile");

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);
        when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFile);
        when(temporaryHoldingFileStorageStrategy.exists(updatedFile)).thenReturn(false);

        when(finalFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(temporaryHoldingFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(scannedFileStorageStrategy.getFile(updatedFile)).thenReturn(serviceSuccess(fileItselfToUpdate));

        when(temporaryHoldingFileStorageStrategy.createFile(eq(updatedFile), isA(File.class))).thenReturn(serviceSuccess(fileItselfToUpdate));
        when(scannedFileStorageStrategy.deleteFile(updatedFile)).thenReturn(serviceSuccess());

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(updatingFileEntry, fakeInputStreamSupplier("Updated content should be here"));

        assertNotNull(result);
        assertTrue(result.isSuccess());

        File newFileResult = result.getSuccessObject().getKey();
        assertEquals("updatedfile", newFileResult.getName());

        assertEquals("/tmp/path/to/updatedfile", newFileResult.getPath());

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).save(fileToUpdate);
        verify(temporaryHoldingFileStorageStrategy).exists(updatedFile);
        verify(finalFileStorageStrategy).getFile(updatedFile);
        verify(scannedFileStorageStrategy).getFile(updatedFile);
        verify(temporaryHoldingFileStorageStrategy).createFile(eq(updatedFile), isA(File.class));
        verify(scannedFileStorageStrategy).deleteFile(updatedFile);
    }

    @Test
    public void testUpdateFileButNoFileExistsOnFilesystemToUpdate() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17);

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();
        FileEntry updatedFileEntry = fileBuilder.with(id(456L)).build();

        FileEntryResource expectedFileResourceForUpdate = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(17).
                build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);
        when(fileEntryRepository.save(fileToUpdate)).thenReturn(updatedFileEntry);
        when(temporaryHoldingFileStorageStrategy.exists(updatedFileEntry)).thenReturn(false);
        when(finalFileStorageStrategy.getFile(updatedFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(temporaryHoldingFileStorageStrategy.getFile(updatedFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(scannedFileStorageStrategy.getFile(updatedFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(quarantinedFileStorageStrategy.getFile(updatedFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(expectedFileResourceForUpdate, fakeInputStreamSupplier());

        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 456L)));

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).save(fileToUpdate);
        verify(temporaryHoldingFileStorageStrategy).exists(updatedFileEntry);
        verify(finalFileStorageStrategy).getFile(updatedFileEntry);
        verify(temporaryHoldingFileStorageStrategy).getFile(updatedFileEntry);
        verify(scannedFileStorageStrategy).getFile(updatedFileEntry);
        verify(quarantinedFileStorageStrategy).getFile(updatedFileEntry);
    }

    @Test
    public void testUpdateFileButNoFileEntryExistsInDatabase() throws IOException {

        FileEntryResource expectedFileResourceForUpdate = newFileEntryResource().with(id(456L)).build();

        when(fileEntryRepository.findOne(456L)).thenReturn(null);

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(expectedFileResourceForUpdate, fakeInputStreamSupplier());

        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 456L)));

        verify(fileEntryRepository).findOne(456L);
    }

    @Test
    public void testUpdateFileWithIncorrectContentLength() throws IOException {

        int incorrectFilesize = 1234;

        FileEntryResource fileResource = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(incorrectFilesize).
                build();

        FileEntryBuilder fileBuilder = newFileEntry().with(id(456L)).withFilesizeBytes(incorrectFilesize);
        FileEntry fileToUpdate = fileBuilder.build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_INCORRECTLY_REPORTED_FILESIZE, 17));

        verify(fileEntryRepository).findOne(456L);
    }

    @Test
    public void testUpdateFileWithIncorrectContentType() throws IOException {

        FileEntryResource fileResource = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(17).
                withMediaType("application/pdf").
                build();

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(17).withMediaType("application/pdf");

        FileEntry fileToUpdate = fileBuilder.with(id(456L)).build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileToUpdate);

        ServiceResult<Pair<File, FileEntry>> result = service.updateFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE, "text/plain"));

        verify(fileEntryRepository).findOne(456L);
    }

    @Test
    public void testDeleteFile() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
        when(finalFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceSuccess(new File("foundme")));
        when(finalFileStorageStrategy.deleteFile(fileEntryToDelete)).thenReturn(serviceSuccess());

        ServiceResult<FileEntry> result = service.deleteFile(456L);
        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(fileEntryRepository).findOne(456L);
        verify(finalFileStorageStrategy).getFile(fileEntryToDelete);
        verify(finalFileStorageStrategy).deleteFile(fileEntryToDelete);
        verify(fileEntryRepository).delete(fileEntryToDelete);
    }

    @Test
    public void testDeleteFileButCantDeleteFileFromFilesystem() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
        when(finalFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceSuccess(new File("cantdeleteme")));
        when(finalFileStorageStrategy.deleteFile(fileEntryToDelete)).thenReturn(serviceFailure(new Error(FILES_UNABLE_TO_DELETE_FILE)));

        ServiceResult<FileEntry> result = service.deleteFile(456L);
        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(FILES_UNABLE_TO_DELETE_FILE)));

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).delete(fileEntryToDelete);
        verify(finalFileStorageStrategy).getFile(fileEntryToDelete);
        verify(finalFileStorageStrategy).deleteFile(fileEntryToDelete);
    }

    @Test
    public void testDeleteFileButNoFileExistsOnFilesystemToDelete() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
        when(finalFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(temporaryHoldingFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(scannedFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(quarantinedFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));

        ServiceResult<FileEntry> result = service.deleteFile(456L);
        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 456L)));

        verify(fileEntryRepository).findOne(456L);
        verify(finalFileStorageStrategy).getFile(fileEntryToDelete);
        verify(temporaryHoldingFileStorageStrategy).getFile(fileEntryToDelete);
        verify(scannedFileStorageStrategy).getFile(fileEntryToDelete);
        verify(quarantinedFileStorageStrategy).getFile(fileEntryToDelete);
    }

    @Test
    public void testDeleteFileChecksEveryFolderForFile() throws IOException {

        FileEntryBuilder fileBuilder = newFileEntry().withFilesizeBytes(30);
        FileEntry fileEntryToDelete = fileBuilder.with(id(456L)).build();

        when(fileEntryRepository.findOne(456L)).thenReturn(fileEntryToDelete);
        when(finalFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(temporaryHoldingFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(scannedFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 456L)));
        when(quarantinedFileStorageStrategy.getFile(fileEntryToDelete)).thenReturn(serviceSuccess(new File("foundme")));
        when(quarantinedFileStorageStrategy.deleteFile(fileEntryToDelete)).thenReturn(serviceSuccess());

        ServiceResult<FileEntry> result = service.deleteFile(456L);
        assertTrue(result.isSuccess());

        verify(fileEntryRepository).findOne(456L);
        verify(fileEntryRepository).delete(fileEntryToDelete);
        verify(finalFileStorageStrategy).getFile(fileEntryToDelete);
        verify(temporaryHoldingFileStorageStrategy).getFile(fileEntryToDelete);
        verify(scannedFileStorageStrategy).getFile(fileEntryToDelete);
        verify(quarantinedFileStorageStrategy).getFile(fileEntryToDelete);
        verify(quarantinedFileStorageStrategy).deleteFile(fileEntryToDelete);
    }

    @Test
    public void testDeleteFileButNoFileEntryExistsInDatabase() throws IOException {

        when(fileEntryRepository.findOne(456L)).thenReturn(null);

        ServiceResult<FileEntry> result = service.deleteFile(456L);
        assertNotNull(result);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 456L)));

        verify(fileEntryRepository).findOne(456L);
    }

    @Test
    public void testGetFileByFileEntryId() throws IOException {

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).build();

        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(quarantinedFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(temporaryHoldingFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(finalFileStorageStrategy.getFile(existingFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 123L)));
        when(scannedFileStorageStrategy.getFile(existingFileEntry)).thenReturn(serviceSuccess(new File("foundme")));

        ServiceResult<Supplier<InputStream>> inputStreamResult = service.getFileByFileEntryId(123L);
        assertTrue(inputStreamResult.isSuccess());

        verify(fileEntryRepository).findOne(123L);
        verify(quarantinedFileStorageStrategy).exists(existingFileEntry);
        verify(temporaryHoldingFileStorageStrategy).exists(existingFileEntry);
        verify(finalFileStorageStrategy).getFile(existingFileEntry);
        verify(scannedFileStorageStrategy).getFile(existingFileEntry);
    }

    @Test
    public void testGetFileByFileEntryIdButFileInQuarantine() throws IOException {

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).build();

        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(quarantinedFileStorageStrategy.exists(existingFileEntry)).thenReturn(true);

        ServiceResult<Supplier<InputStream>> inputStreamResult = service.getFileByFileEntryId(123L);
        assertTrue(inputStreamResult.isFailure());
        assertTrue(inputStreamResult.getFailure().is(forbiddenError(FILES_FILE_QUARANTINED)));

        verify(fileEntryRepository).findOne(123L);
        verify(quarantinedFileStorageStrategy).exists(existingFileEntry);
        verifyNoMoreFileServiceInteractions();
    }

    @Test
    public void testGetFileByFileEntryIdButFileAwaitingScanning() throws IOException {

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).build();

        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(quarantinedFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(temporaryHoldingFileStorageStrategy.exists(existingFileEntry)).thenReturn(true);

        ServiceResult<Supplier<InputStream>> inputStreamResult = service.getFileByFileEntryId(123L);
        assertTrue(inputStreamResult.isFailure());
        assertTrue(inputStreamResult.getFailure().is(forbiddenError(FILES_FILE_AWAITING_VIRUS_SCAN)));

        verify(fileEntryRepository).findOne(123L);
        verify(quarantinedFileStorageStrategy).exists(existingFileEntry);
        verify(temporaryHoldingFileStorageStrategy).exists(existingFileEntry);
    }

    @Test
    public void testGetFileByFileEntryIdAndFileInScannedFolder() throws IOException {

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).build();

        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(quarantinedFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(temporaryHoldingFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(finalFileStorageStrategy.getFile(existingFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 123L)));
        when(scannedFileStorageStrategy.getFile(existingFileEntry)).thenReturn(serviceSuccess(new File("foundme")));

        ServiceResult<Supplier<InputStream>> inputStreamResult = service.getFileByFileEntryId(123L);
        assertTrue(inputStreamResult.isSuccess());

        verify(fileEntryRepository).findOne(123L);
        verify(quarantinedFileStorageStrategy).exists(existingFileEntry);
        verify(temporaryHoldingFileStorageStrategy).exists(existingFileEntry);
        verify(finalFileStorageStrategy).getFile(existingFileEntry);
        verify(scannedFileStorageStrategy).getFile(existingFileEntry);
    }

    @Test
    public void testGetFileByFileEntryIdButFileEntryEntityDoesntExist() throws IOException {

        when(fileEntryRepository.findOne(123L)).thenReturn(null);

        ServiceResult<Supplier<InputStream>> result = service.getFileByFileEntryId(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 123L)));

        verify(fileEntryRepository).findOne(123L);
    }

    @Test
    public void testGetFileByFileEntryIdAndFileIsInFinalStorageLocation() throws IOException {

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).withFilesizeBytes(10).build();
        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(quarantinedFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(temporaryHoldingFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(finalFileStorageStrategy.getFile(existingFileEntry)).thenReturn(serviceSuccess(new File("foundme")));

        ServiceResult<Supplier<InputStream>> result = service.getFileByFileEntryId(123L);
        assertTrue(result.isSuccess());

        verify(fileEntryRepository).findOne(123L);
        verify(quarantinedFileStorageStrategy).exists(existingFileEntry);
        verify(temporaryHoldingFileStorageStrategy).exists(existingFileEntry);
        verify(finalFileStorageStrategy).getFile(existingFileEntry);
    }

    @Test
    public void testGetFileByFileEntryIdButFileDoesntExist() throws IOException {

        FileEntry existingFileEntry = newFileEntry().with(id(123L)).withFilesizeBytes(10).build();
        when(fileEntryRepository.findOne(123L)).thenReturn(existingFileEntry);
        when(quarantinedFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(temporaryHoldingFileStorageStrategy.exists(existingFileEntry)).thenReturn(false);
        when(finalFileStorageStrategy.getFile(existingFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 123L)));
        when(scannedFileStorageStrategy.getFile(existingFileEntry)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 123L)));

        ServiceResult<Supplier<InputStream>> result = service.getFileByFileEntryId(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 123L)));

        verify(fileEntryRepository).findOne(123L);
        verify(quarantinedFileStorageStrategy).exists(existingFileEntry);
        verify(temporaryHoldingFileStorageStrategy).exists(existingFileEntry);
        verify(finalFileStorageStrategy).getFile(existingFileEntry);
        verify(scannedFileStorageStrategy).getFile(existingFileEntry);
    }

    @Test
    public void testCreateFileWithIncorrectContentLength() throws IOException {

        int incorrectFilesize = 1234;

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(incorrectFilesize).
                build();

        ServiceResult<Pair<File, FileEntry>> result = service.createFile(fileResource, fakeInputStreamSupplier());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_INCORRECTLY_REPORTED_FILESIZE, 17));
    }

    @Test
    public void testCreateFileWithIncorrectContentType() throws IOException {

        FileEntryResource fileResource = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(17).
                withMediaType("application/pdf").
                build();

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

    private void verifyNoMoreFileServiceInteractions() {
        verifyNoMoreInteractions(fileEntryRepository, quarantinedFileStorageStrategy, temporaryHoldingFileStorageStrategy, finalFileStorageStrategy, scannedFileStorageStrategy);
    }
}
