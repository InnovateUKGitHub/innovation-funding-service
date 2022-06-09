package org.innovateuk.ifs.filestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ServiceException extends ResponseStatusException {

    public ServiceException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, cause.getMessage(), cause);
    }

    public ServiceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
