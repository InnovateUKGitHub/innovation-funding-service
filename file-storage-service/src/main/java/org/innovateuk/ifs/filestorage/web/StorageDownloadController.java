package org.innovateuk.ifs.filestorage.web;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class StorageDownloadController implements FileDownload {

    @Autowired
    private StorageService storageService;

    @Override
    public ResponseEntity<Resource> fileStreamByUuid(String uuid) {
            FileDownloadResponse fileDownloadResponse = storageService.fileByUuid(uuid);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(fileDownloadResponse.getMimeType()));
            headers.setContentLength(fileDownloadResponse.getFileSizeBytes());
            headers.setContentDisposition(
                ContentDisposition
                    .attachment()
                    .filename(fileDownloadResponse.getFileName())
                    .build()
            );
            return ResponseEntity.ok().headers(headers)
                    .body(new ByteArrayResource(fileDownloadResponse.getPayload()));
    }

    @Override
    public ResponseEntity<FileDownloadResponse> fileDownloadResponse(String uuid) {
        return ResponseEntity.ok(storageService.fileByUuid(uuid));
    }

}
