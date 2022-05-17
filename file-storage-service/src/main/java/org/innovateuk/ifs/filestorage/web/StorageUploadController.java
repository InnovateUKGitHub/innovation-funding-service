package org.innovateuk.ifs.filestorage.web;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@RestController
public class StorageUploadController implements FileUpload {

    @Autowired
    private StorageService storageService;

    @Override
    public ResponseEntity<FileUploadResponse> fileUpload(FileUploadRequest fileUploadRequest) {
        return ResponseEntity.ok(storageService.fileUpload(fileUploadRequest));
    }

    @Override
    public ResponseEntity<FileUploadResponse> fileUploadRaw(byte[] payload) {
        try {
            return fileUpload(FileUploadRequestBuilder.fromResource(payload, "testMethod").build());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
