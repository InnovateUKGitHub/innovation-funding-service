package org.innovateuk.ifs.shibboleth.api.models;

import java.util.Collections;
import java.util.List;

public class ErrorResponse {

    private final String key;
    private final List<String> arguments;


    public ErrorResponse(final String key) {
        this(key, Collections.emptyList());
    }


    public ErrorResponse(final String key, final List<String> arguments) {
        this.key = key;
        this.arguments = arguments;
    }


    public String getKey() {
        return key;
    }


    public List<String> getArguments() {
        return arguments;
    }


    @Override
    public String toString() {
        return "ErrorResponse{" +
            "key='" + key + '\'' +
            ", arguments=" + arguments +
            '}';
    }
}
