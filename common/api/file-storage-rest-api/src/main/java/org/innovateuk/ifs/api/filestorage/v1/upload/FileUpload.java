package org.innovateuk.ifs.api.filestorage.v1.upload;

import org.innovateuk.ifs.api.filestorage.ApiVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(ApiVersion.VERSION_ONE + "/upload")
public interface FileUpload {

    @PostMapping
    ResponseEntity<FileUploadResponse> fileUpload(FileUploadRequest fileUploadRequest);

}
