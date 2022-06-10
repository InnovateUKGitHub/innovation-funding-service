package org.innovateuk.ifs.filestorage.messaging;

import org.innovateuk.ifs.IfsConstants;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.UUID;

import static org.innovateuk.ifs.starters.messaging.cfg.CommonQueues.FILE_UPLOAD_SERVICE_UPLOAD;

public class UploadMessageListener {

    @Autowired
    private StorageService storageService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Scheduled(initialDelay = 1000L, fixedDelay = 20L)
    public void testinit() throws IOException {
        Resource resource = new ClassPathResource("test.jpg");
        byte[] payload = resource.getInputStream().readAllBytes();
        FileUploadRequest fileUploadRequest =  FileUploadRequest.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName("test.jpg")
                .systemId(IfsConstants.IFS_SYSTEM_USER)
                .userId(IfsConstants.IFS_SYSTEM_USER)
                .mimeType(MediaType.IMAGE_JPEG_VALUE)
                .fileSizeBytes(payload.length)
                .md5Checksum(FileHashing.fileHash64(payload))
                .payload(payload).build();
        amqpTemplate.convertAndSend(FILE_UPLOAD_SERVICE_UPLOAD, fileUploadRequest);
    }

    /**
     * Listen the upload queue. The caller will have a UUID to reference to the stored result at a later date.
     * @param fileUploadRequest the file wrapped request to store.
     */
    @RabbitListener(queues = {FILE_UPLOAD_SERVICE_UPLOAD})
    public void fileUpload(FileUploadRequest fileUploadRequest) {
        storageService.fileUpload(fileUploadRequest);
    }

}
