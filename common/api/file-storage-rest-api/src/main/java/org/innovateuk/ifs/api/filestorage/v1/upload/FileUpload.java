package org.innovateuk.ifs.api.filestorage.v1.upload;

import org.innovateuk.ifs.api.filestorage.ApiVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface FileUpload {

    @PostMapping(ApiVersion.VERSION_ONE + "/upload")
    ResponseEntity<FileUploadResponse> fileUpload(FileUploadRequest fileUploadRequest);

}
