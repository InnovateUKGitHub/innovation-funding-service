package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * An exception that is thrown when a user attempts to access a file that has been quarantined and so poses a security risk
 */
public class FileQuarantinedException extends IFSRuntimeException {

    public FileQuarantinedException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}
