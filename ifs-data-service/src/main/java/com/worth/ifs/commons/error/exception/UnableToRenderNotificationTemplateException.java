package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 *
 */
public class UnableToRenderNotificationTemplateException extends RuntimeException {
    public UnableToRenderNotificationTemplateException() {
    }

    public UnableToRenderNotificationTemplateException(String message) {
        super(message);
    }

    public UnableToRenderNotificationTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToRenderNotificationTemplateException(Throwable cause) {
        super(cause);
    }

    public UnableToRenderNotificationTemplateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
