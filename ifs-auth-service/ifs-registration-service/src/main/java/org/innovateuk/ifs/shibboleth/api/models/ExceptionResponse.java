package org.innovateuk.ifs.shibboleth.api.models;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionResponse {

    private final String message;
    private final String stacktrace;


    public ExceptionResponse(final Exception exception) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        this.message = exception.getMessage();
        this.stacktrace = sw.toString();
    }


    public String getMessage() {
        return message;
    }


    public String getStacktrace() {
        return stacktrace;
    }


    @Override
    public String toString() {
        return "ExceptionResponse{" +
            "message='" + message + '\'' +
            ", stacktrace='" + stacktrace + '\'' +
            '}';
    }
}
