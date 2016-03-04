package com.worth.ifs.exception;

import com.worth.ifs.commons.error.exception.IFSRuntimeException;

import java.util.List;

/**
 * Created by rav on 01/03/2016.
 *
 */
public class UnableToReadUploadedFile extends IFSRuntimeException {

    public UnableToReadUploadedFile() {
    }

    public UnableToReadUploadedFile(List<Object> arguments) {
        super(arguments);
    }

    public UnableToReadUploadedFile(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnableToReadUploadedFile(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnableToReadUploadedFile(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnableToReadUploadedFile(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
