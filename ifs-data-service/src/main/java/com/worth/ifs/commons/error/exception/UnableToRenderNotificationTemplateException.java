package com.worth.ifs.commons.error.exception;

import java.util.List;

public class UnableToRenderNotificationTemplateException extends IFSRuntimeException {
    public UnableToRenderNotificationTemplateException() {
    	// no-arg constructor
    }

    public UnableToRenderNotificationTemplateException(List<Object> arguments) {
        super(arguments);
    }

    public UnableToRenderNotificationTemplateException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnableToRenderNotificationTemplateException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnableToRenderNotificationTemplateException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnableToRenderNotificationTemplateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
