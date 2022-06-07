package org.innovateuk.ifs.api.filestorage.v1.upload;

import org.innovateuk.ifs.api.filestorage.ApiConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface FileUpload {

    @PostMapping(value = ApiConstants.VERSION_ONE + ApiConstants.PATH)
    ResponseEntity<FileUploadResponse> fileUpload(@Valid @RequestBody FileUploadRequest fileUploadRequest);

    @PostMapping(value = ApiConstants.VERSION_ONE + ApiConstants.PATH + "/raw")
    ResponseEntity<FileUploadResponse> fileUploadRaw(@RequestBody byte[] payload);

}
