package org.innovateuk.ifs.commons.error.exception;


import java.util.Collections;

/**
 * This exception is handled in the webservice for errors when
 */
public class IncorrectStateForPageException extends IFSRuntimeException {

    public IncorrectStateForPageException(String message) {
        super(message, Collections.emptyList());
    }
}
