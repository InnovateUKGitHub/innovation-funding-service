package org.innovateuk.ifs.granttransfer.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.granttransfer.builder.EuGrantTransferBuilder.newEuGrantTransfer;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EuGrantTransferServiceImplTest extends BaseServiceUnitTest<EuGrantTransferServiceImpl> {

    @Mock
    private EuGrantTransferRepository euGrantTransferRepository;

    @Mock
    private FileEntryService fileEntryServiceMock;

    @Override
    protected EuGrantTransferServiceImpl supplyServiceUnderTest() {
        return new EuGrantTransferServiceImpl();
    }

    @Test
    public void findGrantAgreement() throws Exception {
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
    public void downloadGrantAgreement() throws Exception {
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
    public void deleteGrantAgreement() throws Exception {
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

}