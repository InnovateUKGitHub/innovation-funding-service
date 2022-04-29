package org.innovateuk.ifs.filestorage.web;

import org.apache.http.HttpHeaders;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.util.FileUploadResponseMapper;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.filestorage.util.TestHelper.headerAssert;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StorageDownloadController.class})
@ActiveProfiles({IfsProfileConstants.TEST})
class StorageDownloadControllerTest {

    @Autowired
    private StorageDownloadController storageDownloadController;

    @MockBean
    private StorageService storageService;

    @Test
    void testDownload() throws IOException {
        UUID uuid = UUID.randomUUID();
        FileDownloadResponse fileDownloadResponse = TestHelper.build(uuid,
                new VirusScanResult(VirusScanStatus.VIRUS_FREE, "OK"));
        when(storageService.fileByUuid(uuid.toString())).thenReturn(Optional.of(fileDownloadResponse));
        ResponseEntity<Object> responseEntity = storageDownloadController.fileByUuid(uuid.toString());
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));

        headerAssert(responseEntity, HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG.toString());
        headerAssert(responseEntity, HttpHeaders.CONTENT_LENGTH, String.valueOf(fileDownloadResponse.fileSizeBytes()));
        headerAssert(responseEntity, "Content-Disposition", "attachment; filename=\"" + fileDownloadResponse.fileName() + "\"");
    }

    @Test
    void testDownloadFail() throws IOException {
        when(storageService.fileByUuid("test")).thenReturn(Optional.empty());
        ResponseEntity<Object> responseEntity = storageDownloadController.fileByUuid("test");
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }
}