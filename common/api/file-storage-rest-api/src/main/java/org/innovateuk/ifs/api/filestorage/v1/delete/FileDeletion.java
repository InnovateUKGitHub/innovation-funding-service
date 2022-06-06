package org.innovateuk.ifs.api.filestorage.v1.delete;

import org.innovateuk.ifs.api.filestorage.ApiVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface FileDeletion {

    @PostMapping(value = ApiVersion.VERSION_ONE + "/delete")
    ResponseEntity<FileDeletionResponse> deleteFile(@Valid @RequestBody FileDeletionRequest fileDeletionRequest);

}
