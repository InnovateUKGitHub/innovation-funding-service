package org.innovateuk.ifs.filestorage.web;

import org.apache.http.HttpHeaders;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class StorageDownloadController implements FileDownload {

    @Autowired
    private StorageService storageService;

    @Override
    public ResponseEntity<Object> fileByUuid(String uuid) {
        try {
            Optional<FileDownloadResponse> fileDownloadResponse = storageService.fileByUuid(uuid);
            if (fileDownloadResponse.isPresent()) {
                return ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .header(HttpHeaders.CONTENT_TYPE, fileDownloadResponse.get().mimeType().toString())
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileDownloadResponse.get().fileSizeBytes()))
                        .header("Content-Disposition", "attachment; filename=\"" + fileDownloadResponse.get().fileName() + "\"")
                        .body(new ByteArrayInputStream(fileDownloadResponse.get().payload()));
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
