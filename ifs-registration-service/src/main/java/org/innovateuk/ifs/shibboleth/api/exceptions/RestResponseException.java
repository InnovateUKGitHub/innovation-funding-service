package org.innovateuk.ifs.shibboleth.api.exceptions;

import org.innovateuk.ifs.shibboleth.api.models.ErrorResponse;

import java.util.Collections;
import java.util.List;

public abstract class RestResponseException extends Exception {

    private final String key;
    private final List<String> arguments;


    public RestResponseException(final String key) {
        super(key);

        this.key = key;
        this.arguments = Collections.emptyList();
    }


    public RestResponseException(final String key, final List<String> arguments) {
        super(key);

        this.key = key;
        this.arguments = arguments;
    }


    protected String getKey() {
        return key;
    }


    protected List<String> getArguments() {
        return arguments;
    }


    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(getKey(), getArguments());
    }
}
