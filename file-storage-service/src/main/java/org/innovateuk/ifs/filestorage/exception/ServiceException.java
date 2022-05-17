package org.innovateuk.ifs.filestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ServiceException extends ResponseStatusException {

    public ServiceException(String reason, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause);
    }

    public ServiceException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, cause.getMessage(), cause);
    }
}
