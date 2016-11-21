package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * Represents an error returned by data layer because of an exception accepting an invitation
 */
public class UnableToAcceptInviteException extends IFSRuntimeException {

    public UnableToAcceptInviteException() {
        // no-arg constructor
    }

    public UnableToAcceptInviteException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}