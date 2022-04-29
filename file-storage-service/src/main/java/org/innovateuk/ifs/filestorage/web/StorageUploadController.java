package org.innovateuk.ifs.filestorage.web;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@Slf4j
public class StorageUploadController implements FileUpload {

    @Autowired
    private StorageService storageService;

    @Override
    public ResponseEntity<FileUploadResponse> fileUpload(FileUploadRequest fileUploadRequest) {
        try {
            return ResponseEntity.ok(storageService.fileUpload(fileUploadRequest));
        } catch (IOException e) {
            log.error("Failed to persist", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
