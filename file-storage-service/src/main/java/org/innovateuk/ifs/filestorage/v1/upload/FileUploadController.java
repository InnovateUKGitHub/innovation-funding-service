package org.innovateuk.ifs.filestorage.v1.upload;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.springframework.http.ResponseEntity;

public class FileUploadController implements FileUpload {

    @Override
    public ResponseEntity<FileUploadResponse> fileUpload(FileUploadRequest fileUploadRequest) {
        return null;
    }
}
