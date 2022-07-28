package org.innovateuk.ifs.filestorage.messaging;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.starters.messaging.cfg.CommonQueues.FILE_UPLOAD_SERVICE_UPLOAD;

public class UploadMessageListener {

    @Autowired
    private StorageService storageService;

    /**
     * Listen the upload queue. The caller will have a UUID to reference to the stored result at a later date.
     * @param fileUploadRequest the file wrapped request to store.
     */
    @RabbitListener(queues = {FILE_UPLOAD_SERVICE_UPLOAD})
    public void fileUpload(FileUploadRequest fileUploadRequest) {
        storageService.fileUpload(fileUploadRequest);
    }

}
