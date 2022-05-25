package org.innovateuk.ifs.filestorage.component;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.exception.NoSuchRecordException;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.innovateuk.ifs.filestorage.virusscan.stub.StubScanProvider;
import org.innovateuk.ifs.filestorage.web.StorageDownloadController;
import org.innovateuk.ifs.starter.feign.filestorage.v1.feign.FileDownloadFeign;
import org.innovateuk.ifs.starter.feign.filestorage.v1.feign.FileUploadFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder.DEFAULT_SYSTEM_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(
    webEnvironment =  SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = "FILE_STORAGE_SERVICE_SERVICE_PORT=8888")
@EnableScheduling
@ActiveProfiles({IfsProfileConstants.TEST, IfsProfileConstants.STUB_AV_SCAN, IfsProfileConstants.LOCAL_STORAGE})
@EnableFeignClients(basePackageClasses = {FileDownloadFeign.class, FileUploadFeign.class})
@EnableJpaRepositories(repositoryBaseClass = FileStorageRecord.class)
@Execution(ExecutionMode.CONCURRENT)
class FileStorageServiceTest {

    @Autowired
    private FileUploadFeign fileUpload;

    @Autowired
    private FileDownloadFeign fileDownloadFeign;

    @Autowired
    private StorageDownloadController storageDownloadController;

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;

    @Test
    void testEndToEnd() throws IOException {
        FileUploadRequest fileUploadRequest = FileUploadRequestBuilder.fromResource(
            new ClassPathResource("test.jpg"),
            MediaType.IMAGE_JPEG,
            FileStorageServiceTest.class.getSimpleName()
        ).build();

        ResponseEntity<FileUploadResponse> response = fileUpload.fileUpload(fileUploadRequest);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        FileStorageRecord fileStorageRecord = fileStorageRecordRepository.findById(fileUploadRequest.getFileId()).get();
        assertThat(fileStorageRecord.systemId(), equalTo(DEFAULT_SYSTEM_ID));
        assertThat(fileStorageRecord.userId(), equalTo(FileStorageServiceTest.class.getSimpleName()));
        assertThat(fileStorageRecord.mimeType(), equalTo(MediaType.IMAGE_JPEG_VALUE));
        assertThat(fileStorageRecord.fileSizeBytes(), equalTo(fileUploadRequest.getFileSizeBytes()));
        assertThat(fileStorageRecord.fileName(), equalTo(fileUploadRequest.getFileName()));
        assertThat(fileStorageRecord.md5Checksum(), equalTo(fileUploadRequest.getMd5Checksum()));
        assertThat(fileStorageRecord.storageLocation(), equalTo("/tmp/" + fileUploadRequest.getFileId()));
        assertThat(fileStorageRecord.error(), is(emptyOrNullString()));

        ResponseEntity<Resource> downloadStream = fileDownloadFeign.fileStreamByUuid(fileUploadRequest.getFileId());
        assertThat(downloadStream.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(downloadStream.getBody().contentLength(), equalTo(fileUploadRequest.getFileSizeBytes()));

        ResponseEntity<FileDownloadResponse> download = fileDownloadFeign.fileDownloadResponse(fileUploadRequest.getFileId());
        assertThat(download.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(download.getBody().getMimeType(), equalTo(MediaType.IMAGE_JPEG_VALUE));
        assertThat(download.getBody().getFileSizeBytes(), equalTo(fileUploadRequest.getFileSizeBytes()));
        assertThat(download.getBody().getFileName(), equalTo(fileUploadRequest.getFileName()));
        assertThat(download.getBody().getMd5Checksum(), equalTo(fileUploadRequest.getMd5Checksum()));
        assertThat(download.getBody().getPayload().length, equalTo(Long.valueOf(fileUploadRequest.getFileSizeBytes()).intValue()));
        assertThat(download.getBody().getError(), is(emptyOrNullString()));
    }

    @Test
    void testFileUploadBadMediaTypeEndToEnd() throws IOException {
        FileUploadRequest fileUploadRequest = FileUploadRequestBuilder.fromResource(
                new ClassPathResource("test.jpg"),
                MediaType.IMAGE_GIF,
                FileStorageServiceTest.class.getSimpleName()
        ).build();

        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> fileUpload.fileUpload(fileUploadRequest));
        assertThat(rse.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(rse.getReason(), equalTo("MimeMismatchException->image/jpeg when image/gif was specified"));

        FileStorageRecord fileStorageRecord = fileStorageRecordRepository.findById(fileUploadRequest.getFileId()).get();
        assertThat(fileStorageRecord.systemId(), equalTo(DEFAULT_SYSTEM_ID));
        assertThat(fileStorageRecord.userId(), equalTo(FileStorageServiceTest.class.getSimpleName()));
        assertThat(fileStorageRecord.mimeType(), equalTo(MediaType.IMAGE_GIF_VALUE));
        assertThat(fileStorageRecord.fileSizeBytes(), equalTo(fileUploadRequest.getFileSizeBytes()));
        assertThat(fileStorageRecord.fileName(), equalTo(fileUploadRequest.getFileName()));
        assertThat(fileStorageRecord.md5Checksum(), equalTo(fileUploadRequest.getMd5Checksum()));
        assertThat(fileStorageRecord.storageLocation(), is(emptyOrNullString()));
        assertThat(fileStorageRecord.error(), equalTo("MimeMismatchException->image/jpeg when image/gif was specified"));

        String fileId = fileUploadRequest.getFileId();
        ResponseStatusException noSuchRecordException = assertThrows(ResponseStatusException.class,
                () -> fileDownloadFeign.fileStreamByUuid(fileId));
        assertThat(noSuchRecordException.getStatus(), equalTo(HttpStatus.NOT_FOUND));

        noSuchRecordException = assertThrows(ResponseStatusException.class,
                () -> fileDownloadFeign.fileDownloadResponse(fileId));
        assertThat(noSuchRecordException.getStatus(), equalTo(HttpStatus.NOT_FOUND));

        assertThrows(NoSuchRecordException.class,
                () -> storageDownloadController.fileStreamByUuid(fileId));
    }

    @Test
    void testShortPayload() throws IOException {
        FileUploadRequest fileUploadRequest = FileUploadRequestBuilder.fromResource(
                new ClassPathResource("test.jpg"),
                MediaType.IMAGE_JPEG,
                FileStorageServiceTest.class.getSimpleName()
        ).fileSizeBytes(Long.MAX_VALUE).build();

        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> fileUpload.fileUpload(fileUploadRequest));
        assertThat(rse.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(rse.getReason(),
                equalTo("InvalidUploadException->Payload length 23384 does not match indicated 9223372036854775807"));
    }

    @Test
    void testVirusDetection() throws IOException {
        FileUploadRequest fileUploadRequest = FileUploadRequestBuilder.fromResource(
                new ByteArrayResource(StubScanProvider.EICAR),
                MediaType.IMAGE_JPEG,
                FileStorageServiceTest.class.getSimpleName()
        ).fileSizeBytes(Long.MAX_VALUE).build();

        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> fileUpload.fileUpload(fileUploadRequest));
        assertThat(rse.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(rse.getReason(), equalTo("VirusDetectedException->EICAR TEST FILE"));
    }

}
