package org.innovateuk.ifs.filestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchRecordException extends ResponseStatusException {

    public NoSuchRecordException(String id) {
        super(HttpStatus.NOT_FOUND, NoSuchRecordException.class.getSimpleName() + "->" + id);
    }

}
