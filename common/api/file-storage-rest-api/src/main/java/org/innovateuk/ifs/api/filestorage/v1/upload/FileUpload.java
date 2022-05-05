package org.innovateuk.ifs.api.filestorage.v1.upload;

import org.innovateuk.ifs.api.filestorage.ApiVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public interface FileUpload {

    @PostMapping(value = ApiVersion.VERSION_ONE + "/upload")
    ResponseEntity<FileUploadResponse> fileUpload(@Valid @RequestBody FileUploadRequest fileUploadRequest);

    @PostMapping(value = ApiVersion.VERSION_ONE + "/uploadRaw")
    ResponseEntity<FileUploadResponse> fileUploadRaw(@RequestBody byte[] payload);

}
