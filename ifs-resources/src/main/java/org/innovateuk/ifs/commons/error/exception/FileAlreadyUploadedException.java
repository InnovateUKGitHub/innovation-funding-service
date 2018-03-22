package org.innovateuk.ifs.commons.error.exception;

import java.util.List;

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
