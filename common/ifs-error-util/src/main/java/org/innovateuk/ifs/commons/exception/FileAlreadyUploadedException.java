package org.innovateuk.ifs.commons.exception;

import java.util.List;

/**
 * This exception is thrown when a user tries to upload a file when one has already been uploaded on same form input response.
 * User will be shown a validation error asking they remove existing file first before uploading a different one.
 */
public class FileAlreadyUploadedException extends IFSRuntimeException {

    public FileAlreadyUploadedException() {
        // no-arg constructor
    }

    public FileAlreadyUploadedException(List<Object> arguments) {
        super(arguments);
    }

    public FileAlreadyUploadedException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public FileAlreadyUploadedException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public FileAlreadyUploadedException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public FileAlreadyUploadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
