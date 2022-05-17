package org.innovateuk.ifs.filestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidUploadException extends ResponseStatusException {

    public InvalidUploadException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
