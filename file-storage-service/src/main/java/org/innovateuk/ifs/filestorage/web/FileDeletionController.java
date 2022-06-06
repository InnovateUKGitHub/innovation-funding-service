package org.innovateuk.ifs.filestorage.web;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletion;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletionRequest;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletionResponse;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FileDeletionController implements FileDeletion {

    @Autowired
    private StorageService storageService;

    @Override
    public ResponseEntity<FileDeletionResponse> deleteFile(FileDeletionRequest fileDeletionRequest) {
        return ResponseEntity.ok(storageService.deleteFile(fileDeletionRequest));
    }

}
