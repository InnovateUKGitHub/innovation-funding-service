package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * An exception that is thrown when a user attempts to verify their e-mail address with a token that has expired.
 */
public class RegistrationTokenExpiredException extends IFSRuntimeException {

    public RegistrationTokenExpiredException() {
        // no-arg constructor
    }

    public RegistrationTokenExpiredException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}