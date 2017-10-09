package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.transactional.OverheadFileServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldBuilder.newFinanceRowMetaField;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaValueBuilder.newFinanceRowMetaValue;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceRowBuilder.newProjectFinanceRow;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class OverheadFileServiceImplTest extends BaseServiceUnitTest<OverheadFileServiceImpl> {

    private static String fileMetaFieldType = "file_entry";

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected OverheadFileServiceImpl supplyServiceUnderTest() {
        return new OverheadFileServiceImpl();
    }

    @Test
    public void createFileEntry() throws Exception {
        FileEntryResource fileEntryToCreate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;
        long overheadId = 1L;

        ApplicationFinanceRow overhead = newApplicationFinanceRow().build();
        when(applicationFinanceRowRepositoryMock.findById(overheadId)).thenReturn(overhead);

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField().build();
        when(financeRowMetaFieldRepositoryMock.findByTitle(fileMetaFieldType)).thenReturn(financeRowMetaField);

        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(overhead.getId(), financeRowMetaField.getId())).thenReturn(null);

        FileEntry createdFileEntry = newFileEntry().build();
        ServiceResult<Pair<File, FileEntry>> successfulFileCreationResult = serviceSuccess(Pair.of(new File("createdfile"), createdFileEntry));
        when(fileServiceMock.createFile(fileEntryToCreate, inputStreamSupplier)).thenReturn(successfulFileCreationResult);

        FileEntryResource createdFileEntryResource = newFileEntryResource().build();
        when(fileEntryMapperMock.mapToResource(createdFileEntry)).thenReturn(createdFileEntryResource);

        ServiceResult<FileEntryResource> result = service.createFileEntry(overheadId, fileEntryToCreate, inputStreamSupplier);

        assertTrue(result.isSuccess());
        assertEquals(createdFileEntryResource, result.getSuccessObject());

        verify(fileServiceMock).createFile(fileEntryToCreate, inputStreamSupplier);

        FinanceRowMetaValue expectedFinanceRowMetaValue = new FinanceRowMetaValue(overhead, financeRowMetaField, successfulFileCreationResult.getSuccessObject().getValue().getId().toString());

        ArgumentCaptor<FinanceRowMetaValue> metaValueArgumentCaptor = ArgumentCaptor.forClass(FinanceRowMetaValue.class);
        verify(financeRowMetaValueRepositoryMock).save(metaValueArgumentCaptor.capture());
        FinanceRowMetaValue actualFinanceRowMetaValue = metaValueArgumentCaptor.getValue();

        assertEquals(expectedFinanceRowMetaValue.getId(), actualFinanceRowMetaValue.getId());
        assertEquals(expectedFinanceRowMetaValue.getFinanceRowId(), actualFinanceRowMetaValue.getFinanceRowId());
        assertEquals(expectedFinanceRowMetaValue.getFinanceRowMetaField(), actualFinanceRowMetaValue.getFinanceRowMetaField());
        assertEquals(expectedFinanceRowMetaValue.getValue(), actualFinanceRowMetaValue.getValue());
    }

    @Test
    public void getFileEntryContents() throws Exception {

        Supplier<InputStream> inputStreamSupplier = () -> null;
        long overheadId = 1L;

        ApplicationFinanceRow overhead = newApplicationFinanceRow().build();
        when(applicationFinanceRowRepositoryMock.findById(overheadId)).thenReturn(overhead);

        FileEntry fileEntry = newFileEntry().build();
        when(fileEntryRepositoryMock.findOne(fileEntry.getId())).thenReturn(fileEntry);

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField().build();
        when(financeRowMetaFieldRepositoryMock.findByTitle(fileMetaFieldType)).thenReturn(financeRowMetaField);

        FinanceRowMetaValue financeRowMetaValue = newFinanceRowMetaValue().withValue(fileEntry.getId().toString()).build();
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(overhead.getId(), financeRowMetaField.getId())).thenReturn(financeRowMetaValue);

        ServiceResult<Supplier<InputStream>> successfulFileCreationResult = serviceSuccess(inputStreamSupplier);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(successfulFileCreationResult);

        FileEntryResource createdFileEntryResource = newFileEntryResource().withId(fileEntry.getId()).build();
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(createdFileEntryResource);

        ServiceResult<FileAndContents> result = service.getFileEntryContents(overheadId);

        assertTrue(result.isSuccess());
        assertThat(result.getSuccessObject(), is(instanceOf(BasicFileAndContents.class)));

        verify(fileServiceMock).getFileByFileEntryId(fileEntry.getId());

        assertEquals(fileEntry.getId(), result.getSuccessObject().getFileEntry().getId());
        assertEquals(inputStreamSupplier, result.getSuccessObject().getContentsSupplier());
    }

    @Test
    public void getProjectFileEntryContents() throws Exception {

        Supplier<InputStream> inputStreamSupplier = () -> null;

        long overheadId = 1L;
        long projectOverheadId = 2L;

        ApplicationFinanceRow overhead = newApplicationFinanceRow().withId(overheadId).build();

        when(projectFinanceRowRepositoryMock.findOne(projectOverheadId)).thenReturn(newProjectFinanceRow().withOriginalApplicationFinanceRow(overhead).build());

        when(applicationFinanceRowRepositoryMock.findById(overheadId)).thenReturn(overhead);

        FileEntry fileEntry = newFileEntry().build();
        when(fileEntryRepositoryMock.findOne(fileEntry.getId())).thenReturn(fileEntry);

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField().build();
        when(financeRowMetaFieldRepositoryMock.findByTitle(fileMetaFieldType)).thenReturn(financeRowMetaField);

        FinanceRowMetaValue financeRowMetaValue = newFinanceRowMetaValue().withValue(fileEntry.getId().toString()).build();
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(overhead.getId(), financeRowMetaField.getId())).thenReturn(financeRowMetaValue);

        ServiceResult<Supplier<InputStream>> successfulFileCreationResult = serviceSuccess(inputStreamSupplier);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(successfulFileCreationResult);

        FileEntryResource createdFileEntryResource = newFileEntryResource().withId(fileEntry.getId()).build();
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(createdFileEntryResource);

        ServiceResult<FileAndContents> result = service.getProjectFileEntryContents(projectOverheadId);

        assertTrue(result.isSuccess());
        assertThat(result.getSuccessObject(), is(instanceOf(BasicFileAndContents.class)));

        verify(fileServiceMock).getFileByFileEntryId(fileEntry.getId());

        assertEquals(fileEntry.getId(), result.getSuccessObject().getFileEntry().getId());
        assertEquals(inputStreamSupplier, result.getSuccessObject().getContentsSupplier());
    }

    @Test
    public void getFileEntryDetails() throws Exception {
        long overheadId = 1L;

        ApplicationFinanceRow overhead = newApplicationFinanceRow().build();
        when(applicationFinanceRowRepositoryMock.findById(overheadId)).thenReturn(overhead);

        FileEntry fileEntry = newFileEntry().build();
        when(fileEntryRepositoryMock.findOne(fileEntry.getId())).thenReturn(fileEntry);

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField().build();
        when(financeRowMetaFieldRepositoryMock.findByTitle(fileMetaFieldType)).thenReturn(financeRowMetaField);

        FinanceRowMetaValue financeRowMetaValue = newFinanceRowMetaValue().withValue(fileEntry.getId().toString()).build();
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(overhead.getId(), financeRowMetaField.getId())).thenReturn(financeRowMetaValue);

        FileEntryResource retrievedFileEntryResource = newFileEntryResource().withId(fileEntry.getId()).build();
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(retrievedFileEntryResource);

        ServiceResult<FileEntryResource> result = service.getFileEntryDetails(overheadId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObject(), retrievedFileEntryResource);

        verify(fileEntryRepositoryMock).findOne(fileEntry.getId());
    }

    @Test
    public void getProjectFileEntryDetails() throws Exception {
        long overheadId = 1L;
        long projectOverheadId = 2L;

        ApplicationFinanceRow overhead = newApplicationFinanceRow().withId(overheadId).build();

        when(projectFinanceRowRepositoryMock.findOne(projectOverheadId)).thenReturn(newProjectFinanceRow().withOriginalApplicationFinanceRow(overhead).build());

        when(applicationFinanceRowRepositoryMock.findById(overheadId)).thenReturn(overhead);

        FileEntry fileEntry = newFileEntry().build();
        when(fileEntryRepositoryMock.findOne(fileEntry.getId())).thenReturn(fileEntry);

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField().build();
        when(financeRowMetaFieldRepositoryMock.findByTitle(fileMetaFieldType)).thenReturn(financeRowMetaField);

        FinanceRowMetaValue financeRowMetaValue = newFinanceRowMetaValue().withValue(fileEntry.getId().toString()).build();
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(overhead.getId(), financeRowMetaField.getId())).thenReturn(financeRowMetaValue);

        FileEntryResource retrievedFileEntryResource = newFileEntryResource().withId(fileEntry.getId()).build();
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(retrievedFileEntryResource);

        ServiceResult<FileEntryResource> result = service.getProjectFileEntryDetails(projectOverheadId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObject(), retrievedFileEntryResource);

        verify(fileEntryRepositoryMock).findOne(fileEntry.getId());
    }

    @Test
    public void updateFileEntry() throws Exception {
        FileEntryResource fileEntryToCreate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;
        long overheadId = 1L;

        ApplicationFinanceRow overhead = newApplicationFinanceRow().build();
        when(applicationFinanceRowRepositoryMock.findById(overheadId)).thenReturn(overhead);

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField().build();
        when(financeRowMetaFieldRepositoryMock.findByTitle(fileMetaFieldType)).thenReturn(financeRowMetaField);

        FinanceRowMetaValue financeRowMetaValue = newFinanceRowMetaValue().withValue("2").build();
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(overhead.getId(), financeRowMetaField.getId())).thenReturn(financeRowMetaValue);

        FileEntry createdFileEntry = newFileEntry().build();
        ServiceResult<Pair<File, FileEntry>> successfulFileCreationResult = serviceSuccess(Pair.of(new File("createdfile"), createdFileEntry));
        when(fileServiceMock.updateFile(fileEntryToCreate, inputStreamSupplier)).thenReturn(successfulFileCreationResult);

        FileEntryResource createdFileEntryResource = newFileEntryResource().build();
        when(fileEntryMapperMock.mapToResource(createdFileEntry)).thenReturn(createdFileEntryResource);

        ServiceResult<FileEntryResource> result = service.createFileEntry(overheadId, fileEntryToCreate, inputStreamSupplier);

        assertTrue(result.isSuccess());
        assertEquals(createdFileEntryResource, result.getSuccessObject());

        verify(fileServiceMock).updateFile(fileEntryToCreate, inputStreamSupplier);

        ArgumentCaptor<FinanceRowMetaValue> metaValueArgumentCaptor = ArgumentCaptor.forClass(FinanceRowMetaValue.class);
        verify(financeRowMetaValueRepositoryMock, never()).save(metaValueArgumentCaptor.capture());
    }

    @Test
    public void deleteFileEntry() throws Exception {
        long overheadId = 1L;
        long fileId = 3L;

        ApplicationFinanceRow overhead = newApplicationFinanceRow().build();
        when(applicationFinanceRowRepositoryMock.findById(overheadId)).thenReturn(overhead);

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField().build();
        when(financeRowMetaFieldRepositoryMock.findByTitle(fileMetaFieldType)).thenReturn(financeRowMetaField);

        FinanceRowMetaValue financeRowMetaValue = newFinanceRowMetaValue().withValue(String.valueOf(fileId)).build();
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(overhead.getId(), financeRowMetaField.getId())).thenReturn(financeRowMetaValue);

        FileEntry fileEntryToDelete = newFileEntry().build();
        when(fileServiceMock.deleteFileIgnoreNotFound(fileId)).thenReturn(serviceSuccess(fileEntryToDelete));

        ServiceResult<Void> result = service.deleteFileEntry(overheadId);

        assertTrue(result.isSuccess());

        verify(fileServiceMock).deleteFileIgnoreNotFound(fileId);
        verify(financeRowMetaValueRepositoryMock).delete(financeRowMetaValue.getId());
    }
}