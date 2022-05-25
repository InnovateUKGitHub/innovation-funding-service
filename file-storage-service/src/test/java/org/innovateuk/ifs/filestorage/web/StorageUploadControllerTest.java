package org.innovateuk.ifs.filestorage.web;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.exception.ServiceException;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.util.FileUploadResponseMapper;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StorageUploadController.class})
@ActiveProfiles({IfsProfileConstants.TEST})
class StorageUploadControllerTest {

    @Autowired
    private StorageUploadController storageUploadController;

    @MockBean
    private StorageService storageService;

    @Test
    @ResourceLock("LOCK")
    void testUpload() throws IOException {
        FileUploadRequest fileUploadRequest = TestHelper.build();
        when(storageService.fileUpload(fileUploadRequest)).thenReturn(FileUploadResponseMapper.build(fileUploadRequest));

        ResponseEntity<FileUploadResponse> responseResponseEntity = storageUploadController.fileUpload(fileUploadRequest);
        assertThat(responseResponseEntity.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(responseResponseEntity.getBody().getMd5Checksum(), equalTo(fileUploadRequest.getMd5Checksum()));
    }

    @Test
    @ResourceLock("LOCK")
    void testUploadFail() throws IOException {
        FileUploadRequest fileUploadRequest = TestHelper.build();
        when(storageService.fileUpload(fileUploadRequest)).thenThrow(new ServiceException(new IOException("ddd")));
        assertThrows(
                ServiceException.class,
                () -> storageUploadController.fileUpload(fileUploadRequest)
        );
    }

}