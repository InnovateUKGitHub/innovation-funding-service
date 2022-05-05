package org.innovateuk.ifs.filestorage.web;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class StorageDownloadController implements FileDownload {

    @Autowired
    private StorageService storageService;

    @Override
    public ResponseEntity<Resource> fileByUuid(String uuid) {
        try {
            Optional<FileDownloadResponse> fileDownloadResponse = storageService.fileByUuid(uuid);
            if (fileDownloadResponse.isPresent()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(fileDownloadResponse.get().mimeType()));
                headers.setContentLength(fileDownloadResponse.get().fileSizeBytes());
                headers.setContentDisposition(
                    ContentDisposition
                        .attachment()
                        .filename(fileDownloadResponse.get().fileName())
                        .build()
                );
                return ResponseEntity.ok().headers(headers)
                        .body(new ByteArrayResource(fileDownloadResponse.get().payload()));
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
