package ifs.test;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Slf4j
public class ScheduledServiceCall {

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    private FileDownload fileDownload;

    @Scheduled(fixedDelay = 1000)
    public void testRun() throws IOException {
        byte[] payload = "{foo:bar}".getBytes(StandardCharsets.UTF_8);
        FileUploadRequest fileUploadRequest = FileUploadRequest.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName("foo.json")
                .md5Checksum(FileHashing.fileHash64(payload))
                .mimeType(MediaType.APPLICATION_JSON_VALUE)
                .payload(payload)
                .userId("test")
                .fileSizeBytes(payload.length)
                .systemId("test").build();
        ResponseEntity<FileUploadResponse> response = fileUpload.fileUpload(fileUploadRequest);
        log.error(response.toString());
        ResponseEntity<FileDownloadResponse> download = fileDownload.fileDownloadResponse(response.getBody().getFileId());
        log.error(download.toString());
    }

}
