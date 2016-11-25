package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * An exception that indicates that a user has attempted to access a file that has not yet been scanned and so could
 * potentially pose a security risk
 */
public class FileAwaitingVirusScanException extends IFSRuntimeException {

    public FileAwaitingVirusScanException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}
