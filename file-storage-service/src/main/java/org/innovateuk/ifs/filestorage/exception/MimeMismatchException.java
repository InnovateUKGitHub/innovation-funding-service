package org.innovateuk.ifs.filestorage.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MimeMismatchException extends ResponseStatusException {

    public MimeMismatchException(String message) {
        super(HttpStatus.BAD_REQUEST, MimeMismatchException.class.getSimpleName() + "->" + message);
    }
}