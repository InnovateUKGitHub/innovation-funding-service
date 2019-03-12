package org.innovateuk.ifs.granttransfer.transactional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.mapper.EuGrantTransferMapper;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.granttransfer.builder.EuGrantTransferBuilder.newEuGrantTransfer;
import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;
import static org.innovateuk.ifs.granttransfer.transactional.EuGrantTransferServiceImpl.HORIZON_2020_START_DATE;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class EuGrantTransferServiceImplTest extends BaseServiceUnitTest<EuGrantTransferServiceImpl> {

    @Mock
    private EuGrantTransferRepository euGrantTransferRepository;

    @Mock
    private FileEntryService fileEntryServiceMock;

    @Mock
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Mock
    private EuGrantTransferMapper mapper;

    @Override
    protected EuGrantTransferServiceImpl supplyServiceUnderTest() {
        return new EuGrantTransferServiceImpl();
    }

    @Test
    public void findGrantAgreement() {
        long applicationId = 1L;
        FileEntry fileEntry = newFileEntry().build();
        EuGrantTransfer grantTransfer = newEuGrantTransfer()
                .withGrantAgreement(fileEntry)
                .build();
        FileEntryResource fileEntryResource = newFileEntryResource().build();
        when(euGrantTransferRepository.findByApplicationId(applicationId)).thenReturn(grantTransfer);
        when(fileEntryServiceMock.findOne(fileEntry.getId())).thenReturn(serviceSuccess(fileEntryResource));

        FileEntryResource response = service.findGrantAgreement(applicationId).getSuccess();

        assertEquals(fileEntryResource, response);
    }

    @Test
    public void downloadGrantAgreement() {
        final long applicationId = 1L;
        final long fileId = 2L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        EuGrantTransfer grantTransfer = newEuGrantTransfer()
                .withGrantAgreement(fileEntry)
                .build();
        FileEntryResource fileEntryResource = new FileEntryResource();
        fileEntryResource.setId(fileId);

        when(euGrantTransferRepository.findByApplicationId(applicationId)).thenReturn(grantTransfer);
        when(fileEntryServiceMock.findOne(fileId)).thenReturn(serviceSuccess(fileEntryResource));
        final Supplier<InputStream> contentSupplier = () -> null;
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(ServiceResult.serviceSuccess(contentSupplier));

        FileAndContents fileAndContents = service.downloadGrantAgreement(applicationId).getSuccess();

        assertEquals(fileAndContents.getContentsSupplier(), contentSupplier);
        assertEquals(fileAndContents.getFileEntry(), fileEntryResource);
    }

    @Test
    public void deleteGrantAgreement() {
        final long applicationId = 1L;
        final long fileId = 101L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        EuGrantTransfer grantTransfer = newEuGrantTransfer()
                .withGrantAgreement(fileEntry)
                .build();

        when(euGrantTransferRepository.findByApplicationId(applicationId)).thenReturn(grantTransfer);
        when(fileServiceMock.deleteFileIgnoreNotFound(fileId)).thenReturn(ServiceResult.serviceSuccess(fileEntry));

        ServiceResult<Void> response = service.deleteGrantAgreement(applicationId);

        assertTrue(response.isSuccess());
        assertNull(grantTransfer.getGrantAgreement());
        verify(fileServiceMock).deleteFileIgnoreNotFound(fileId);
    }

    @Test
    public void uploadGrantAgreement() {
        FileControllerUtils fileControllerUtils = mock(FileControllerUtils.class);
        Long maxFileSize = 2L;
        List<String> validMediaTypes = asList("PDF");
        setField(service, "fileControllerUtils", fileControllerUtils);
        setField(service, "maxFileSize", maxFileSize);
        setField(service, "validMediaTypes", validMediaTypes);

        String contentType = "contentType";
        String contentLength = "1";
        String originalFilename = "filename";
        long applicationId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);

        EuGrantTransfer grantTransfer = newEuGrantTransfer()
                .build();
        FileEntry created = newFileEntry().build();
        Pair<File, FileEntry> result = ImmutablePair.of(mock(File.class), created);
        when(euGrantTransferRepository.findByApplicationId(applicationId)).thenReturn(grantTransfer);
        when(fileControllerUtils.handleFileUpload(eq(contentType), eq(contentLength), eq(originalFilename), eq(fileValidator), eq(validMediaTypes), eq(maxFileSize), eq(request), any()))
                .thenReturn(restSuccess(Void.class));
        FileHeaderAttributes attributes = mock(FileHeaderAttributes.class);
        Supplier<InputStream> inputStreamSupplier = mock(Supplier.class);
        FileEntryResource fileEntryResource = newFileEntryResource().build();
        when(attributes.toFileEntryResource()).thenReturn(fileEntryResource);

        ServiceResult<Void> response = service.uploadGrantAgreement(contentType, contentLength, originalFilename, applicationId, request);

        assertTrue(response.isSuccess());

        ArgumentCaptor<BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<Void>>> argument = ArgumentCaptor.forClass(BiFunction.class);
        verify(fileControllerUtils).handleFileUpload(eq(contentType), eq(contentLength), eq(originalFilename), eq(fileValidator), eq(validMediaTypes), eq(maxFileSize), eq(request), argument.capture());

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(result));
        argument.getValue().apply(attributes, inputStreamSupplier);

        assertEquals(created, grantTransfer.getGrantAgreement());
    }

    @Test
    public void getGrantTransferByApplicationId() {
        long applicationId = 1L;

        EuGrantTransfer grantTransfer = newEuGrantTransfer()
                .build();
        EuGrantTransferResource grantTransferResource = newEuGrantTransferResource().build();
        when(euGrantTransferRepository.findByApplicationId(applicationId)).thenReturn(grantTransfer);
        when(mapper.mapToResource(grantTransfer)).thenReturn(grantTransferResource);

        ServiceResult<EuGrantTransferResource> result = service.getGrantTransferByApplicationId(applicationId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), grantTransferResource);
    }

    @Test
    public void updateGrantTransferByApplicationId() {
        long applicationId = 1L;
        EuActionTypeResource euActionTypeResource = new EuActionTypeResource();
        euActionTypeResource.setId(2L);
        EuGrantTransferResource grantTransferResource = newEuGrantTransferResource()
                .withActionType(euActionTypeResource)
                .withFundingContribution(BigDecimal.TEN)
                .withGrantAgreementNumber("123456")
                .withParticipantId("987654321")
                .withProjectCoordinator(true)
                .withProjectEndDate(HORIZON_2020_START_DATE.plusMonths(2))
                .withProjectStartDate(LocalDate.now().minusDays(1))
                .withProjectName("Project name")
                .build();

        Application application = newApplication().build();
        EuGrantTransfer grantTransfer = newEuGrantTransfer()
                .withApplication(application)
                .build();

        when(euGrantTransferRepository.findByApplicationId(applicationId)).thenReturn(grantTransfer);

        ServiceResult<Void> result = service.updateGrantTransferByApplicationId(grantTransferResource, applicationId);

        assertTrue(result.isSuccess());

        assertEquals(grantTransfer.getActionType().getId(), euActionTypeResource.getId());
        assertEquals(grantTransfer.getGrantAgreementNumber(), grantTransferResource.getGrantAgreementNumber());
        assertEquals(grantTransfer.getFundingContribution(), grantTransferResource.getFundingContribution());
        assertEquals(grantTransfer.getParticipantId(), grantTransferResource.getParticipantId());
        assertEquals(grantTransfer.getProjectEndDate(), grantTransferResource.getProjectEndDate());
        assertEquals(grantTransfer.getProjectStartDate(), grantTransferResource.getProjectStartDate());
        assertEquals(grantTransfer.getProjectCoordinator(), grantTransferResource.getProjectCoordinator());

        assertEquals(application.getName(), grantTransferResource.getProjectName());
        assertEquals(application.getStartDate(), HORIZON_2020_START_DATE);
        assertEquals(application.getDurationInMonths(), (Long) 2L);

    }

}