package org.innovateuk.ifs.filestorage.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class VirusDetectedException extends ResponseStatusException {

    public VirusDetectedException(String message) {
        super(HttpStatus.BAD_REQUEST, VirusDetectedException.class.getSimpleName() + "->" + message);
    }
}
