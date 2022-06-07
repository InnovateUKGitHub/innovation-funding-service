package org.innovateuk.ifs.api.filestorage.v1.delete;

import org.innovateuk.ifs.api.filestorage.ApiConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface FileDeletion {

    @DeleteMapping(value = ApiConstants.VERSION_ONE + ApiConstants.PATH)
    ResponseEntity<FileDeletionResponse> deleteFile(@Valid @RequestBody FileDeletionRequest fileDeletionRequest);

}
