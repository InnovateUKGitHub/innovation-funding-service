package org.innovateuk.ifs.shibboleth.api.exceptions;

import org.innovateuk.ifs.shibboleth.api.models.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;


public class InvalidPasswordException extends RestResponseException {

    public static final String ERROR_KEY = "INVALID_PASSWORD";

    public InvalidPasswordException(final List<String> problems) {
        super(ERROR_KEY, problems);
    }

    public List<ErrorResponse> getErrorResponses() {
        return getArguments()
                .stream()
                .map(ErrorResponse::new)
                .collect(Collectors.toList());
    }
}
