package org.innovateuk.ifs.granttransfer.transactional;

import com.google.common.io.ByteStreams;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.config.FileValidationConfig;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.service.ByFormInputMediaTypesGenerator;
import org.innovateuk.ifs.file.transactional.FileEntryServiceImpl;
import org.innovateuk.ifs.file.transactional.FileServiceImpl;
import org.innovateuk.ifs.file.transactional.FileServiceTransactionHelper;
import org.innovateuk.ifs.file.transactional.gluster.GlusterFileServiceImpl;
import org.innovateuk.ifs.form.transactional.FormInputServiceImpl;
import org.innovateuk.ifs.granttransfer.controller.EuGrantTransferController;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.mockbean.MockBeanTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.DelegatingServletInputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Import({FileValidationConfig.class})
@SpringBootTest(classes = {EuGrantTransferServiceImpl.class, ByFormInputMediaTypesGenerator.class,
        FormInputServiceImpl.class, FormInputServiceImpl.class, EuGrantTransferController.class,
        FileControllerUtils.class, FileServiceImpl.class, FileEntryServiceImpl.class})
public class UploadGrantAgreementComponentTest extends MockBeanTest {

    @Autowired
    private EuGrantTransferController euGrantTransferController;

    @MockBean // Feign microservice call
    private FileDownload fileDownload;

    @MockBean // Feign microservice call
    private FileUpload fileUpload;

    @MockBean // Db layer helper
    private FileServiceTransactionHelper fileServiceTransactionHelper;

    @Autowired // MockBean via MockBeanRepositoryConfiguration
    private EuGrantTransferRepository euGrantTransferRepository;

    @MockBean // Gluster not available for local testing
    private GlusterFileServiceImpl glusterFileService;

    @Test
    public void uploadGrantAgreement() throws IOException {
        String contentType = MediaType.APPLICATION_PDF_VALUE;
        String originalFilename = "filename";
        long applicationId = 1L;
        String fileId = UUID.randomUUID().toString();

        Resource resource = new ClassPathResource("webtest.pdf");
        byte[] payload = ByteStreams.toByteArray(resource.getInputStream());

        mockFileServiceHelper(fileId);
        mockEuGrantTransfer(applicationId);
        HttpServletRequest request = mockHttpServletRequest(payload);

        FileUploadResponse fileUploadResponse = fileUploadResponse(fileId, originalFilename, contentType, payload);
        when(fileUpload.fileUpload(any())).thenReturn(ResponseEntity.ok(fileUploadResponse));

        RestResult<Void> response = euGrantTransferController.uploadGrantAgreement(contentType, "" + payload.length, originalFilename, applicationId, request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    private FileUploadResponse fileUploadResponse(String fileId, String originalFilename, String contentType, byte[] payload) {
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setFileId(fileId);
        fileUploadResponse.setFileName(originalFilename);
        fileUploadResponse.setMd5Checksum(FileHashing.fileHash64(payload));
        fileUploadResponse.setMimeType(contentType);
        fileUploadResponse.setFileSizeBytes(payload.length);
        return fileUploadResponse;
    }

    private HttpServletRequest mockHttpServletRequest(byte[] content) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(byteArrayInputStream);
        when(request.getInputStream()).thenReturn(servletInputStream);
        return request;
    }

    private void mockFileServiceHelper(String fileId) {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileUuid(fileId);
        fileEntry.setId(1L);
        when(fileServiceTransactionHelper.persistInitial()).thenReturn(fileEntry);
        when(fileServiceTransactionHelper.updateResponse(any(), any(), any(), any(), any())).thenReturn(fileEntry);
    }

    private void mockEuGrantTransfer(Long applicationId) {
        EuGrantTransfer grantTransfer = new EuGrantTransfer();
        Application application = new Application();
        application.setId(applicationId);
        grantTransfer.setApplication(application);
        when(euGrantTransferRepository.save(any())).thenReturn(grantTransfer);
    }

}
