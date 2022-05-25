package org.innovateuk.ifs.filestorage.web;

import org.apache.http.HttpHeaders;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.filestorage.exception.NoSuchRecordException;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.filestorage.util.TestHelper.headerAssert;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StorageDownloadController.class})
@ActiveProfiles({IfsProfileConstants.TEST})
@Execution(ExecutionMode.SAME_THREAD)
class StorageDownloadControllerTest {

    @Autowired
    private StorageDownloadController storageDownloadController;

    @MockBean
    private StorageService storageService;

    @Test
    void testDownload() throws IOException {
        UUID uuid = UUID.randomUUID();
        FileDownloadResponse fileDownloadResponse = TestHelper.buildDownLoadResponse(uuid);
        when(storageService.fileByUuid(uuid.toString())).thenReturn(fileDownloadResponse);
        ResponseEntity<Resource> responseEntity = storageDownloadController.fileStreamByUuid(uuid.toString());
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));

        headerAssert(responseEntity, HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG.toString());
        headerAssert(responseEntity, HttpHeaders.CONTENT_LENGTH, String.valueOf(fileDownloadResponse.getFileSizeBytes()));
        headerAssert(responseEntity, "Content-Disposition", "attachment; filename=\"" + fileDownloadResponse.getFileName() + "\"");
    }

    @Test
    void testDownloadResponse() throws IOException {
        UUID uuid = UUID.randomUUID();
        FileDownloadResponse fileDownloadResponse = TestHelper.buildDownLoadResponse(uuid);
        when(storageService.fileByUuid(uuid.toString())).thenReturn(fileDownloadResponse);
        ResponseEntity<FileDownloadResponse> responseEntity = storageDownloadController.fileDownloadResponse(uuid.toString());
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(responseEntity.getBody().getMimeType(), equalTo(MediaType.IMAGE_JPEG.toString()));
    }

    @Test
    void testDownloadFail() throws IOException {
        when(storageService.fileByUuid("test")).thenThrow(new NoSuchRecordException("test"));
        assertThrows(NoSuchRecordException.class, () -> storageDownloadController.fileStreamByUuid("test"));
    }
}