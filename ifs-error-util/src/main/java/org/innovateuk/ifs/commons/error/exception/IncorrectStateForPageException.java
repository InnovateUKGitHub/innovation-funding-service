package org.innovateuk.ifs.commons.error.exception;


import java.util.Collections;

/**
 * This exception is used when a webservice endpoint is not available in the current state.
 * It will be handled with 404 error.
 */
public class IncorrectStateForPageException extends IFSRuntimeException {

    public IncorrectStateForPageException(String message) {
        super(message, Collections.emptyList());
    }
}
