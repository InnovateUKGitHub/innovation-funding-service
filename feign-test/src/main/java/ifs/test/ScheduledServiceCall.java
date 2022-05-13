package ifs.test;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Slf4j
public class ScheduledServiceCall {

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    private FileDownload fileDownload;

    @Scheduled(fixedDelay = 1000)
    public void testRun() throws IOException {
        ResponseEntity<FileUploadResponse> response = fileUpload.fileUploadRaw("This is some text here".getBytes(StandardCharsets.UTF_8));
        log.error(response.toString());
        ResponseEntity<Optional<FileDownloadResponse>> download = fileDownload.fileDownloadResponse(response.getBody().getFileId());
        log.error(download.toString());
    }

}
