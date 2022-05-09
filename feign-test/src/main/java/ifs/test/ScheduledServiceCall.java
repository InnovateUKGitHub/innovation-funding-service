package ifs.test;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ScheduledServiceCall {

    @Autowired
    private FileUpload fileUpload;

    @Scheduled(fixedDelay = 1000)
    public void testRun() {
        ResponseEntity<FileUploadResponse> response = fileUpload.fileUploadRaw("123123".getBytes(StandardCharsets.UTF_8));
        log.error(response.getStatusCode().getReasonPhrase());

    }

}
