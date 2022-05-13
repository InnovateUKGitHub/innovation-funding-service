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
    public ResponseEntity<Resource> fileStreamByUuid(String uuid) {
        try {
            Optional<FileDownloadResponse> fileDownloadResponse = storageService.fileByUuid(uuid);
            if (fileDownloadResponse.isPresent()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(fileDownloadResponse.get().getMimeType()));
                headers.setContentLength(fileDownloadResponse.get().getFileSizeBytes());
                headers.setContentDisposition(
                    ContentDisposition
                        .attachment()
                        .filename(fileDownloadResponse.get().getFileName())
                        .build()
                );
                return ResponseEntity.ok().headers(headers)
                        .body(new ByteArrayResource(fileDownloadResponse.get().getPayload()));
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Optional<FileDownloadResponse>> fileDownloadResponse(String uuid) {
        try {
            return ResponseEntity.ok(storageService.fileByUuid(uuid));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
