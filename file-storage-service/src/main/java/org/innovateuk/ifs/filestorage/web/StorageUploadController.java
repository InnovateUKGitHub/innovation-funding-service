package org.innovateuk.ifs.filestorage.web;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.upload.*;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class StorageUploadController implements FileUpload {

    @Autowired
    private StorageService storageService;

    @Override
    public ResponseEntity<FileUploadResponse> fileUpload(FileUploadRequest fileUploadRequest) {
        return ResponseEntity.ok(storageService.fileUpload(fileUploadRequest));
    }

    /**
     * Keep this in until end of load testing...
     */
    @Override
    public ResponseEntity<FileUploadResponse> fileUploadRaw(byte[] payload) {
        FileUploadRequest fileUploadRequest = FileUploadRequest.builder()
                .fileSizeBytes(payload.length)
                .fileName("testupload.pdf")
                .fileId(UUID.randomUUID().toString())
                .systemId(StorageUploadController.class.getSimpleName())
                .mimeType(MediaType.APPLICATION_PDF_VALUE)
                .md5Checksum(FileHashing.fileHash64(payload))
                .userId(StorageUploadController.class.getSimpleName())
                .payload(payload)
                .build();
        return fileUpload(fileUploadRequest);
    }

    @Override
    public ResponseEntity<FileDeletionResponse> deleteFile(FileDeletionRequest fileDeletionRequest) {
        return ResponseEntity.ok(storageService.deleteFile(fileDeletionRequest));
    }

}
