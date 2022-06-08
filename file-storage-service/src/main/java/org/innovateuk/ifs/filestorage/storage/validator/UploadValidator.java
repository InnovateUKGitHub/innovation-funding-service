package org.innovateuk.ifs.filestorage.storage.validator;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.exception.InvalidUploadException;

public class UploadValidator {

    public void validateFile(FileUploadRequest fileUploadRequest) throws InvalidUploadException {
        if (fileUploadRequest.getPayload().length != fileUploadRequest.getFileSizeBytes()) {
            throw new InvalidUploadException(
                    String.format(
                            "Payload length %1$s does not match indicated %2$s",
                            fileUploadRequest.getPayload().length,
                            fileUploadRequest.getFileSizeBytes()));
        }
    }

}
